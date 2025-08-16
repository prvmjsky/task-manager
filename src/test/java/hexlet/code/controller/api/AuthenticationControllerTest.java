package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import hexlet.code.utils.ModelGenerator;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "command.line.runner.enabled=false",
    "application.runner.enabled=false"})
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Mock
    private JwtDecoder jwtDecoder;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper om;

    private User newUser;

    @BeforeEach
    public void setUp() {

        userRepository.deleteAll();

        newUser = Instancio.of(modelGenerator.getUserModel()).create();
    }

    @Test
    public void testLogin() throws Exception {

        // Using service instead of direct saving to repo for password encoding
        userService.createUser(newUser);

        var loginData = Map.of(
            "username", newUser.getUsername(),
            "password", newUser.getPassword()
        );

        var request = post("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(loginData));
        mockMvc.perform(request).andExpect(status().isOk());
    }
}
