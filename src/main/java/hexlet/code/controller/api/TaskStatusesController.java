package hexlet.code.controller.api;

import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusDTO;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.exception.EntityExistsException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskStatusRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/task_statuses")
public class TaskStatusesController {

    @Autowired
    private TaskStatusRepository repository;

    @Autowired
    private TaskStatusMapper mapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TaskStatusDTO> index() {
        return repository.findAll().stream()
            .map(mapper::map)
            .toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusDTO show(@PathVariable Long id) {

        var model = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(String.format("Task status with id %d not found", id)));

        return mapper.map(model);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatusDTO create(@Valid @RequestBody TaskStatusCreateDTO dto) {

        if (repository.existsBySlug(dto.getSlug())) {
            throw new EntityExistsException("Task status with this slug already exists");
        }

        var model = mapper.map(dto);
        repository.save(model);
        return mapper.map(model);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusDTO update(@Valid @RequestBody TaskStatusUpdateDTO dto, @PathVariable Long id) {

        var model = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(String.format("Task status with id %d not found", id)));

        mapper.update(dto, model);
        repository.save(model);
        return mapper.map(model);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
