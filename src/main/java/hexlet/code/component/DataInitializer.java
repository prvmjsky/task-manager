package hexlet.code.component;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

import java.util.Map;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        var user = new UserCreateDTO();
        user.setEmail("hexlet@example.com");
        user.setPassword("qwerty");
        userService.create(user);

        var defaultStatuses = Map.of(
            "Draft", "draft",
            "ToReview", "to_review",
            "ToBeFixed", "to_be_fixed",
            "ToPublish", "to_publish",
            "Published", "published");

        defaultStatuses.forEach((name, slug) -> {
            var status = new TaskStatus();
            status.setName(name);
            status.setSlug(slug);
            taskStatusRepository.save(status);
        });
    }
}
