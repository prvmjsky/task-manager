package hexlet.code.controller.api;

import hexlet.code.dto.UserDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserMapper mapper;

    @GetMapping
    public List<UserDTO> index() {
        return repository.findAll().stream()
            .map(mapper::map)
            .toList();
    }
}
