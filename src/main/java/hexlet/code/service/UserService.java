package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.List;

public class UserService implements UserDetailsManager {

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserMapper mapper;

    @Autowired
    private PasswordEncoder encoder;

    public List<UserDTO> findAll() {
        return repository.findAll().stream()
            .map(mapper::map)
            .toList();
    }

    public UserDTO findById(Long id) {

        var model = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %d not found", id)));

        return mapper.map(model);
    }

    public UserDTO create(UserCreateDTO dto) {
        var model = mapper.map(dto);
        model.setPasswordDigest(encoder.encode(dto.getPassword()));
        repository.save(model);
        return mapper.map(model);
    }

    public UserDTO update(UserUpdateDTO dto, Long id) {

        var model = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %d not found", id)));

        mapper.update(dto, model);
        model.setPasswordDigest(encoder.encode(dto.getPassword().get()));

        repository.save(model);
        return mapper.map(model);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public void createUser(UserDetails userData) {
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
