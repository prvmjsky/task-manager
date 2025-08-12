package hexlet.code.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class WelcomeController {

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to Spring";
    }

    @GetMapping("/")
    public RedirectView root() {
        var redirect = new RedirectView("/welcome");
        redirect.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        return redirect;
    }
}
