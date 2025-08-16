package hexlet.code.component;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
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

    @Autowired
    private LabelRepository labelRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        var admin = new UserCreateDTO();
        admin.setEmail("hexlet@example.com");
        admin.setPassword("qwerty");
        userService.create(admin);

        var defaultStatuses = Map.of(
            "Draft", "draft",
            "ToReview", "to_review",
            "ToBeFixed", "to_be_fixed",
            "ToPublish", "to_publish",
            "Published", "published");

        defaultStatuses.forEach((name, slug) -> {
            var status = new TaskStatus(name, slug);
            taskStatusRepository.save(status);
        });

        labelRepository.save(new Label("bug"));
        labelRepository.save(new Label("feature"));
    }
}
