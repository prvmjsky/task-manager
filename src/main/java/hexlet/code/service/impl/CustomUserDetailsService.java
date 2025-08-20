package hexlet.code.service.impl;

import hexlet.code.exception.EntityExistsException;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsManager {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public void createUser(UserDetails userData) {

        if (userExists(userData.getUsername())) {
            throw new EntityExistsException("User with this username already exists");
        }

        var user = new User();
        user.setEmail(userData.getUsername());
        var passwordDigest = encoder.encode(userData.getPassword());
        user.setPasswordDigest(passwordDigest);
        repository.save(user);
    }

    @Override
    public void deleteUser(String username) {
        repository.deleteByEmail(username);
    }

    @Override
    public boolean userExists(String username) {
        return repository.existsByEmail(username);
    }

    @Override
    public void updateUser(UserDetails user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'changePassword'");
    }
}
