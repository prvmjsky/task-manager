package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class LabelsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    private JwtRequestPostProcessor adminToken;

    private Task testTask;

    private TaskStatus testTaskStatus;

    private User testUser;

    private Label testLabel;

    @BeforeEach
    public void setUp() {

        adminToken = jwt().jwt(builder -> builder.subject("hexlet@example.com"));

        testTask = Instancio.of(modelGenerator.getTaskModel()).create();
        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();

        taskStatusRepository.save(testTaskStatus);
        userRepository.save(testUser);

        testTask.setTaskStatus(testTaskStatus);
        testTask.setAssignee(testUser);

        taskRepository.save(testTask);
        labelRepository.save(testLabel);
    }

    @Test
    public void showTest() throws Exception {

        var response = mockMvc.perform(get("/api/labels/" + testLabel.getId())
                .with(adminToken))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertThatJson(response.getContentAsString()).and(
            v -> v.node("name").isEqualTo(testLabel.getName())
        );
    }

    @Test
    public void indexTest() throws Exception {

        var response = mockMvc.perform(get("/api/labels")
                .with(adminToken))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertThat(response).contains(testLabel.getName());
    }

    @Test
    public void createTest() throws Exception {

        var dto = new LabelCreateDTO();
        dto.setName("Red Label");

        mockMvc.perform(post("/api/labels")
                .with(adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto)))
            .andExpect(status().isCreated());

        var label = labelRepository.findByName(dto.getName()).orElseThrow();
        assertThat(label.getName()).isEqualTo(dto.getName());
    }

    @Test
    public void updateTest() throws Exception {

        var dto = new LabelUpdateDTO();
        dto.setName(JsonNullable.of("Black Label"));

        mockMvc.perform(put("/api/labels/" + testLabel.getId())
                .with(adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto)))
            .andExpect(status().isOk());

        assertThat(labelRepository.findByName(dto.getName().get())).isPresent();
    }

    @Test
    public void deleteTest() throws Exception {

        mockMvc.perform(delete("/api/labels/" + testLabel.getId())
                .with(adminToken))
            .andExpect(status().isNoContent());

        assertThat(labelRepository.existsById(testLabel.getId())).isFalse();
    }

    @Test
    public void unauthorizedTest() throws Exception {

        mockMvc.perform(get("/api/labels"))
            .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/labels/" + testLabel.getId()))
            .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/labels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(testLabel)))
            .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/api/labels/" + testTask.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(testLabel)))
            .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/api/labels/" + testLabel.getId()))
            .andExpect(status().isUnauthorized());
    }
}
