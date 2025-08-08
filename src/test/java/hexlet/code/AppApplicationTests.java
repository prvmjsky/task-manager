package hexlet.code;

import hexlet.code.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AppApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testDataInitialization() {
        assertTrue(userRepository.findByEmail("hexlet@example.com").isPresent());
    }

}
