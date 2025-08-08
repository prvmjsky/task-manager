package hexlet.code.component;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var user = new User();
        user.setEmail("hexlet@example.com");
        user.setPassword("qwerty");
        userRepository.save(user);
    }
}
