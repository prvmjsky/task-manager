package hexlet.code.controller.api;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "command.line.runner.enabled=false",
    "application.runner.enabled=false"})
@AutoConfigureMockMvc
@Transactional
@Rollback
public class TasksFilteredIndexTest {

    @Autowired
    private MockMvc mockMvc;

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

    private Label label1;
    private Label label2;

    private TaskStatus status1;
    private TaskStatus status2;

    private User user1;
    private User user2;

    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    public void setUp() {

        adminToken = jwt().jwt(builder -> builder.subject("hexlet@example.com"));

        label1 = Instancio.create(modelGenerator.getLabelModel());
        labelRepository.save(label1);
        label2 = Instancio.create(modelGenerator.getLabelModel());
        labelRepository.save(label2);

        status1 = Instancio.create(modelGenerator.getTaskStatusModel());
        taskStatusRepository.save(status1);
        status2 = Instancio.create(modelGenerator.getTaskStatusModel());
        taskStatusRepository.save(status2);

        user1 = Instancio.create(modelGenerator.getUserModel());
        userRepository.save(user1);
        user2 = Instancio.create(modelGenerator.getUserModel());
        userRepository.save(user2);

        task1 = createTask("Name1", "Description1", status1, user1, Set.of(label1));
        task2 = createTask("Name2", "Description2", status2, user2, Set.of(label2));
        task3 = createTask("Name3", "Description3", status2, user1, Set.of(label2));
    }

    private Task createTask(String name, String description, TaskStatus status, User assignee, Set<Label> labels) {
        Task task = new Task();
        task.setName(name);
        task.setDescription(description);
        task.setTaskStatus(status);
        task.setAssignee(assignee);
        task.setLabelsUsed(labels);
        return taskRepository.save(task);
    }

    @Test
    public void testNoFilter() throws Exception {

        var responseWithoutFilter = mockMvc.perform(get("/api/tasks").with(adminToken))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        assertThat(responseWithoutFilter).contains("Name1", "Name2", "Name3");
    }

    @Test
    public void testTitleCont() throws Exception {

        var result = mockMvc.perform(get("/api/tasks?titleCont=ame3").with(adminToken))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        assertThat(result).doesNotContain(task1.getName(), task2.getName()).contains(task3.getName());
        assertThat(result).contains(task3.getDescription()).doesNotContain(task1.getDescription());

        var emptyResult = mockMvc.perform(get("/api/tasks?titleCont=4").with(adminToken))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        assertThatJson(emptyResult).isArray().isEmpty();
    }

    @Test
    public void testAssignee() throws Exception {

        var resultAdmin = mockMvc.perform(get("/api/tasks?assigneeId=" + user1.getId()).with(adminToken))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        assertThat(resultAdmin).contains("Name1", "Name3").doesNotContain("Name2");

        var resultUser = mockMvc.perform(get("/api/tasks?assigneeId=" + user2.getId()).with(adminToken))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        assertThat(resultUser).contains("Name2").doesNotContain("Name1", "Name3");
    }

    @Test
    public void testStatus() throws Exception {

        var resultDraft = mockMvc.perform(get("/api/tasks?status=" + status1.getSlug()).with(adminToken))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        assertThat(resultDraft).contains("Name1").doesNotContain("Name2", "Name3");

        var resultReview = mockMvc.perform(get("/api/tasks?status=" + status2.getSlug()).with(adminToken))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        assertThat(resultReview).contains("Name2", "Name3").doesNotContain("Name1");

        var resultEmpty = mockMvc.perform(get("/api/tasks?status=slugslugslug").with(adminToken))
            .andExpect(status().isOk());

        var emptyResult = resultEmpty.andReturn().getResponse().getContentAsString();
        assertThatJson(emptyResult).isArray().isEmpty();
    }
}
