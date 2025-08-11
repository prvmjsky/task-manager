package hexlet.code.component;

import hexlet.code.dto.users.UserCreateDTO;
import hexlet.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private final UserService userService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var user = new UserCreateDTO();
        user.setEmail("hexlet@example.com");
        user.setPassword("qwerty");
        userService.create(user);
    }
}
