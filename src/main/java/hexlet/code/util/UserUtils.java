package hexlet.code.util;

import hexlet.code.exception.NoSuchUserException;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserUtils {

    @Autowired
    private UserRepository userRepository;

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        var email = authentication.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new NoSuchUserException("No user found with this name"));
    }

    public boolean isCurrentUserId(Long id) {
        var currentUserId = getCurrentUser().getId();
        return Objects.equals(currentUserId, id);
    }

    public boolean isCurrentUserName(String name) {
        var currentUserName = getCurrentUser().getUsername();
        return Objects.equals(currentUserName, name);
    }
}
