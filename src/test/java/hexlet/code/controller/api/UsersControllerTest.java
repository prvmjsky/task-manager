package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
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
import java.util.Map;

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
public class UsersControllerTest {

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

    private JwtRequestPostProcessor token;

    private User testUser;

    private UserUpdateDTO testUserUpdateDTO;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
            .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
            .apply(springSecurity())
            .build();

        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));

        testUserUpdateDTO = new UserUpdateDTO();
        testUserUpdateDTO.setFirstName(JsonNullable.of("Somebody"));
        testUserUpdateDTO.setEmail(JsonNullable.of("once@told.me"));
        testUserUpdateDTO.setPassword(JsonNullable.of("TheWorldIsGonnaRollMe"));
    }

    @Test
    public void testIndex() throws Exception {

        userRepository.save(testUser);

        var response = mockMvc.perform(get("/api/users").with(jwt()))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();
        var body = response.getContentAsString();

        List<UserDTO> userDTOS = om.readValue(body, new TypeReference<>() {
        });

        var actual = userDTOS.stream().map(userMapper::map).toList();
        var expected = userRepository.findAll();
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testShow() throws Exception {

        userRepository.save(testUser);

        var response = mockMvc.perform(get("/api/users/" + testUser.getId()).with(jwt()))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();
        var body = response.getContentAsString();

        assertThatJson(body).and(
            v -> v.node("email").isEqualTo(testUser.getEmail()),
            v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
            v -> v.node("lastName").isEqualTo(testUser.getLastName())
        );
    }

    @Test
    public void testCreate() throws Exception {

        var request = post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(testUser));

        var response = mockMvc.perform(request.with(token))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse();
        var body = response.getContentAsString();

        assertThatJson(body).and(
            v -> v.node("email").isEqualTo(testUser.getEmail()),
            v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
            v -> v.node("lastName").isEqualTo(testUser.getLastName())
        );

        var id = om.readTree(body).path("id").asLong();
        assertThat(userRepository.existsById(id)).isTrue();
        assertThat(userRepository.existsByEmail(testUser.getEmail())).isTrue();
    }

    @Test
    public void testCreateUserMethod() throws Exception {
        userService.createUser(testUser);
        assertThat(userRepository.existsByEmail(testUser.getEmail())).isTrue();
    }

    @Test
    public void testUpdate() throws Exception {

        userRepository.save(testUser);

        var request = put("/api/users/" + testUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(testUserUpdateDTO));

        mockMvc.perform(request.with(token))
            .andExpect(status().isOk());

        var user = userRepository.findById(testUser.getId())
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("User with id %d not found", testUser.getId())));

        assertThat(user.getFirstName()).isEqualTo(testUserUpdateDTO.getFirstName().get());
        assertThat(user.getEmail()).isEqualTo(testUserUpdateDTO.getEmail().get());
    }

    @Test
    public void testDelete() throws Exception {
        userRepository.save(testUser);
        var request = delete("/api/users/" + testUser.getId());
        mockMvc.perform(request.with(token))
            .andExpect(status().isNoContent());
        assertThat(userRepository.existsById(testUser.getId())).isFalse();
    }

    @Test
    public void testUnauthorizedRights() throws Exception {

        mockMvc.perform(get("/welcome")).andExpect(status().isOk());

        userRepository.save(testUser);
        var testUserId = testUser.getId();

        var deleteRequest = delete("/api/users/" + testUserId);
        mockMvc.perform(deleteRequest).andExpect(status().isUnauthorized());
        assertThat(userRepository.existsById(testUserId)).isTrue();

        var putRequest = put("/api/users/" + testUserId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(testUserUpdateDTO));
        mockMvc.perform(putRequest).andExpect(status().isUnauthorized());

        var user = userRepository.findById(testUserId)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("User with id %d not found", testUserId)));
        assertThat(user.getFirstName()).isEqualTo(testUser.getFirstName());
        assertThat(user.getEmail()).isEqualTo(testUser.getEmail());
    }

    @Test
    public void testAuthorizedRights() throws Exception {

        var dto = new UserCreateDTO();
        dto.setEmail(testUser.getEmail());
        dto.setPassword(testUser.getPassword());
        userService.create(dto);

        var loginData = Map.of(
            "username", testUser.getUsername(),
            "password", testUser.getPassword()
        );

        var request = post("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(loginData));
        mockMvc.perform(request).andExpect(status().isOk());
    }

    @Test
    public void testForbidden() throws Exception {

        userRepository.save(testUser);
        var testUserId = testUser.getId();

        var loginData = Map.of(
            "username", "malicious@stranger.su",
            "password", "DoNotTrustMe666"
        );

        post("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(loginData));

        var wrongToken = jwt().jwt(builder -> builder.subject(loginData.get("username")));

        var deleteRequest = delete("/api/users/" + testUserId);
        mockMvc.perform(deleteRequest.with(wrongToken)).andExpect(status().isForbidden());
        assertThat(userRepository.existsById(testUserId)).isTrue();


        var putRequest = put("/api/users/" + testUserId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(testUserUpdateDTO));
        mockMvc.perform(putRequest.with(wrongToken)).andExpect(status().isForbidden());

        var user = userRepository.findById(testUserId)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("User with id %d not found", testUserId)));
        assertThat(user.getFirstName()).isEqualTo(testUser.getFirstName());
        assertThat(user.getEmail()).isEqualTo(testUser.getEmail());
    }
}
