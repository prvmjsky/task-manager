package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "command.line.runner.enabled=false",
    "application.runner.enabled=false"})
@AutoConfigureMockMvc
@Transactional
@Rollback
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

    private User testUser;
    private User newUser;

    private JwtRequestPostProcessor testUserToken;
    private JwtRequestPostProcessor newUserToken;

    @BeforeEach
    public void setUp() {
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);

        newUser = Instancio.of(modelGenerator.getUserModel()).create();

        testUserToken = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
        newUserToken = jwt().jwt(builder -> builder.subject(newUser.getEmail()));
    }

    @Test
    public void testIndex() throws Exception {

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
            .content(om.writeValueAsString(newUser));

        var response = mockMvc.perform(request.with(jwt()))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse();
        var body = response.getContentAsString();

        assertThatJson(body).and(
            v -> v.node("email").isEqualTo(newUser.getEmail()),
            v -> v.node("firstName").isEqualTo(newUser.getFirstName()),
            v -> v.node("lastName").isEqualTo(newUser.getLastName())
        );

        var id = om.readTree(body).path("id").asLong();
        assertThat(userRepository.existsById(id)).isTrue();
        assertThat(userRepository.existsByEmail(testUser.getEmail())).isTrue();
    }

    @Test
    public void testUpdate() throws Exception {

        var dto = new UserUpdateDTO();
        dto.setFirstName(JsonNullable.of("Somebody"));
        dto.setLastName(JsonNullable.undefined());
        dto.setEmail(JsonNullable.undefined());
        dto.setPassword(JsonNullable.of("OnceToldMe"));

        var request = put("/api/users/" + testUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(dto));

        mockMvc.perform(request.with(testUserToken))
            .andExpect(status().isOk());

        var foundUser = userRepository.findById(testUser.getId());
        assertThat(foundUser.isPresent()).isTrue();
        assertThat(foundUser.get().getFirstName()).isEqualTo(dto.getFirstName().get());
        assertThat(foundUser.get().getEmail()).isEqualTo(testUser.getEmail());
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/api/users/" + testUser.getId()).with(testUserToken))
            .andExpect(status().isNoContent());
        assertThat(userRepository.existsById(testUser.getId())).isFalse();
    }

    @Test
    public void testUnauthorizedRights() throws Exception {

        mockMvc.perform(get("/welcome")).andExpect(status().isOk());

        var testUserId = testUser.getId();

        var deleteRequest = delete("/api/users/" + testUserId);
        mockMvc.perform(deleteRequest).andExpect(status().isUnauthorized());
        assertThat(userRepository.existsById(testUserId)).isTrue();

        var putRequest = put("/api/users/" + testUserId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(Map.of("email", "doesitmatter@any.way")));
        mockMvc.perform(putRequest).andExpect(status().isUnauthorized());

        var foundUser = userRepository.findById(testUserId);
        assertThat(foundUser.isPresent()).isTrue();
        assertThat(foundUser.get().getFirstName()).isEqualTo(testUser.getFirstName());
    }

    @Test
    public void testForbidden() throws Exception {

        var testUserId = testUser.getId();

        post("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(newUser));

        var deleteRequest = delete("/api/users/" + testUserId);
        mockMvc.perform(deleteRequest.with(newUserToken))
            .andExpect(status().isForbidden());
        assertThat(userRepository.existsById(testUserId)).isTrue();

        var putRequest = put("/api/users/" + testUserId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(Map.of("email", "doesitmatter@any.way")));
        mockMvc.perform(putRequest.with(newUserToken))
            .andExpect(status().isForbidden());

        var user = userRepository.findById(testUserId)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("User with id %d not found", testUserId)));
        assertThat(user.getFirstName()).isEqualTo(testUser.getFirstName());
    }

    // NOT A CONTROLLER METHOD but uses the similar data to be tested
    @Test
    public void testCreateUserMethodFromUserService() throws Exception {
        userService.createUser(newUser);
        assertThat(userRepository.existsByEmail(newUser.getEmail())).isTrue();
    }
}
