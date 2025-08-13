//package hexlet.code.controller.api;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import hexlet.code.dto.taskstatus.TaskStatusDTO;
//import hexlet.code.mapper.TaskStatusMapper;
//import hexlet.code.model.TaskStatus;
//import hexlet.code.repository.TaskStatusRepository;
//import hexlet.code.utils.ModelGenerator;
//import org.instancio.Instancio;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.test.web.servlet.request
//    .SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
//import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest(properties = {
//    "command.line.runner.enabled=false",
//    "application.runner.enabled=false"})
//@AutoConfigureMockMvc
//public class TaskStatusesControllerTest {
//
//    @Autowired
//    private WebApplicationContext wac;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ModelGenerator modelGenerator;
//
//    @Autowired
//    private TaskStatusRepository taskStatusRepository;
//
//    @Autowired
//    private TaskStatusMapper taskStatusMapper;
//
//    @Autowired
//    private ObjectMapper om;
//
//    @Mock
//    private JwtDecoder jwtDecoder;
//
//    private JwtRequestPostProcessor adminToken;
//
//    private TaskStatus testTaskStatus;
//
//    @BeforeEach
//    public void setUp() {
//        taskStatusRepository.deleteAll();
//
//        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
//            .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
//            .apply(springSecurity())
//            .build();
//
//        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
//        taskStatusRepository.save(testTaskStatus);
//
//        adminToken = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
//    }
//
//    @Test
//    public void testIndex() throws Exception {
//
//        taskStatusRepository.save(testTaskStatus);
//
//        var response = mockMvc.perform(get("/api/task_statuses").with(jwt()))
//            .andExpect(status().isOk())
//            .andReturn()
//            .getResponse();
//        var body = response.getContentAsString();
//
//        List<TaskStatusDTO> statuses = om.readValue(body, new TypeReference<>() {
//        });
//
//        var actual = statuses.stream().map(taskStatusMapper::map).toList();
//        var expected = taskStatusRepository.findAll();
//        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
//    }
//
//    @Test
//    public void testShow() throws Exception {
//
//    }
//
//    @Test
//    public void testCreate() throws Exception {
//
//    }
//
//    @Test
//    public void testUpdate() throws Exception {
//
//    }
//
//    @Test
//    public void testDelete() throws Exception {
//
//    }
//
//    @Test
//    public void testUnauthorizedRights() throws Exception {
//
//    }
//
//    @Test
//    public void testAuthorizedRights() throws Exception {
//
//    }
//}
