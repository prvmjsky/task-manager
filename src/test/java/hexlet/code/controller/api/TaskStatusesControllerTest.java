package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.taskstatus.TaskStatusDTO;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.utils.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request
    .SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "command.line.runner.enabled=false",
    "application.runner.enabled=false"})
@AutoConfigureMockMvc
public class TaskStatusesControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    @Autowired
    private ObjectMapper om;

    @Mock
    private JwtDecoder jwtDecoder;

    private JwtRequestPostProcessor adminToken;

    private TaskStatus testTaskStatus;
    private TaskStatus newStatus;

    @BeforeEach
    public void setUp() {

        taskStatusRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
            .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
            .apply(springSecurity())
            .build();

        adminToken = jwt().jwt(builder -> builder.subject("hexlet@example.com"));

        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        taskStatusRepository.save(testTaskStatus);

        newStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
    }

    @Test
    public void testIndex() throws Exception {

        var response = mockMvc.perform(get("/api/task_statuses").with(jwt()))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();
        var body = response.getContentAsString();

        List<TaskStatusDTO> statuses = om.readValue(body, new TypeReference<>() {
        });

        var actual = statuses.stream().map(taskStatusMapper::map).toList();
        var expected = taskStatusRepository.findAll();
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testShow() throws Exception {

        var response = mockMvc.perform(get("/api/task_statuses/" + testTaskStatus.getId()).with(jwt()))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();
        var body = response.getContentAsString();

        assertThatJson(body).and(
            v -> v.node("name").isEqualTo(testTaskStatus.getName()),
            v -> v.node("slug").isEqualTo(testTaskStatus.getSlug())
        );
    }

    @Test
    public void testCreate() throws Exception {

        var request = post("/api/task_statuses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(newStatus));

        var response = mockMvc.perform(request.with(adminToken))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse();
        var body = response.getContentAsString();

        assertThatJson(body).and(
            v -> v.node("name").isEqualTo(newStatus.getName()),
            v -> v.node("slug").isEqualTo(newStatus.getSlug())
        );

        var id = om.readTree(body).path("id").asLong();
        assertThat(taskStatusRepository.existsById(id)).isTrue();
        assertThat(taskStatusRepository.existsBySlug(newStatus.getSlug())).isTrue();
    }

    @Test
    public void testUpdate() throws Exception {

        var dto = new TaskStatusUpdateDTO();
        dto.setName(JsonNullable.of("toBe"));
        dto.setSlug(JsonNullable.undefined());

        var request = put("/api/task_statuses/" + testTaskStatus.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(dto));

        mockMvc.perform(request.with(adminToken))
            .andExpect(status().isOk());

        var taskStatus = taskStatusRepository.findById(testTaskStatus.getId())
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("Task status with id %d not found", testTaskStatus.getId())));

        assertThat(taskStatus.getName()).isEqualTo(dto.getName().get());
        assertThat(taskStatus.getSlug()).isEqualTo(testTaskStatus.getSlug());
    }

    @Test
    public void testDelete() throws Exception {

        mockMvc.perform(delete("/api/task_statuses/" + testTaskStatus.getId()).with(adminToken))
            .andExpect(status().isNoContent());
        assertThat(taskStatusRepository.existsById(testTaskStatus.getId())).isFalse();
    }

    @Test
    public void testUnauthorizedRights() throws Exception {

        var dto = new TaskStatusUpdateDTO();
        dto.setName(JsonNullable.of("toBe"));
        dto.setSlug(JsonNullable.undefined());

        var testTaskStatusId = testTaskStatus.getId();

        var deleteRequest = delete("/api/task_statuses/" + testTaskStatusId);

        mockMvc.perform(deleteRequest).andExpect(status().isUnauthorized());
        assertThat(taskStatusRepository.existsById(testTaskStatusId)).isTrue();

        var postRequest = post("/api/task_statuses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(newStatus));

        mockMvc.perform(postRequest).andExpect(status().isUnauthorized());
        assertThat(newStatus.getId()).isNull();

        var putRequest = put("/api/task_statuses/" + testTaskStatusId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(dto));

        mockMvc.perform(putRequest).andExpect(status().isUnauthorized());
        var foundStatus = taskStatusRepository.findById(testTaskStatusId);
        assertThat(foundStatus.isPresent()).isTrue();
        assertThat(foundStatus.get().getSlug()).isEqualTo(testTaskStatus.getSlug());

    }
}
