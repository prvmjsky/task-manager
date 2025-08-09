package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.ModelGenerator;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "command.line.runner.enabled=false",
    "application.runner.enabled=false" })
@AutoConfigureMockMvc
public class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserMapper userMapper;

    private User testUser;


    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
    }

    @Test
    public void testIndex() throws Exception {

        userRepository.save(testUser);

        var response = mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();
        var body = response.getContentAsString();

        List<UserDTO> userDTOS = om.readValue(body, new TypeReference<>() { });

        var actual = userDTOS.stream().map(userMapper::map).toList();
        var expected = userRepository.findAll();
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testShow() throws Exception {

        userRepository.save(testUser);

        var response = mockMvc.perform(get("/api/users/" + testUser.getId()))
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

        var response = mockMvc.perform(request)
            .andExpect(status().isCreated())
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
    public void testUpdate() throws Exception {

        userRepository.save(testUser);

        var dto = new UserUpdateDTO();
        dto.setFirstName(JsonNullable.of("Somebody"));
        dto.setEmail(JsonNullable.of("once@told.me"));
        dto.setPassword(JsonNullable.of("TheWorldIsGonnaRollMe"));

        var request = put("/api/users/" + testUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(dto));

        mockMvc.perform(request).andExpect(status().isOk());

        var user = userRepository.findById(testUser.getId())
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("User with id %d not found", testUser.getId())));

        assertThat(user.getFirstName()).isEqualTo(dto.getFirstName().get());
        assertThat(user.getEmail()).isEqualTo(dto.getEmail().get());
        assertThat(user.getPassword()).isEqualTo(dto.getPassword().get());
    }

    @Test
    public void testDelete() throws Exception {
        userRepository.save(testUser);
        var request = delete("/api/users/" + testUser.getId());
        mockMvc.perform(request).andExpect(status().isNoContent());
        assertThat(userRepository.existsById(testUser.getId())).isFalse();
    }
}
