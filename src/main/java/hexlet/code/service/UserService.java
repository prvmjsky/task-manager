package hexlet.code.service;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.exception.EntityExistsException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.UserUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsManager {

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserMapper mapper;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserUtils utils;

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

    public UserDTO create(@Valid UserCreateDTO dto) {

        if (userExists(dto.getEmail())) {
            throw new EntityExistsException("User with this email already exists");
        }

        var model = mapper.map(dto);
        model.setPasswordDigest(encoder.encode(dto.getPassword()));
        repository.save(model);
        return mapper.map(model);
    }

    @PreAuthorize("@userUtils.isAdmin() or @userUtils.isCurrentUserId(#id)")
    public UserDTO update(@Valid UserUpdateDTO dto, Long id) {

        var model = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %d not found", id)));

        mapper.update(dto, model);
        model.setPasswordDigest(encoder.encode(dto.getPassword().get()));

        repository.save(model);
        return mapper.map(model);
    }

    @PreAuthorize("@userUtils.isAdmin() or @userUtils.isCurrentUserId(#id)")
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public void createUser(@Valid UserDetails userData) {

        if (userExists(userData.getUsername())) {
            throw new EntityExistsException("User with this username already exists");
        }

        var user = new User();
        user.setEmail(userData.getUsername());
        var passwordDigest = encoder.encode(userData.getPassword());
        user.setPasswordDigest(passwordDigest);
        repository.save(user);
    }

    @PreAuthorize("@userUtils.isAdmin() or @userUtils.isCurrentUserId(#id)")
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
