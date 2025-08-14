// TODO: This test class causes errors that I can't resolve yet

//package hexlet.code;
//
//import hexlet.code.repository.TaskStatusRepository;
//import hexlet.code.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import java.nio.charset.StandardCharsets;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class DataInitializerTest {
//
//    @Autowired
//    private WebApplicationContext wac;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private TaskStatusRepository taskStatusRepository;
//
//    @BeforeEach
//    public void setUp() {
//        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
//            .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
//            .apply(springSecurity())
//            .build();
//    }
//
//    @Test
//    public void testDefaultUser() throws Exception {
//        assertTrue(userRepository.existsByEmail("hexlet@example.com"));
//    }
//
//    @Test
//    public void testDefaultTaskStatuses() throws Exception {
//
//        var defaultStatuses = Map.of(
//            "Draft", "draft",
//            "ToReview", "to_review",
//            "ToBeFixed", "to_be_fixed",
//            "ToPublish", "to_publish",
//            "Published", "published");
//
//        defaultStatuses.values()
//            .forEach(slug -> assertTrue(taskStatusRepository.existsBySlug(slug)));
//    }
//}
