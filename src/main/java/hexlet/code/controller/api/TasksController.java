package hexlet.code.controller.api;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.dto.task.TaskUpdateDTO;

import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.specification.TaskSpecification;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TasksController {

    @Autowired
    private TaskRepository repository;

    @Autowired
    private TaskMapper mapper;

    @Autowired
    private TaskSpecification specBuilder;

    @GetMapping
    public ResponseEntity<List<TaskDTO>> index(
        TaskParamsDTO params,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "15") int pageSize) {

        var spec = specBuilder.build(params);
        var pageParams = PageRequest.of(page - 1, pageSize);
        var tasks = repository.findAll(spec, pageParams).map(mapper::map).toList();

        return ResponseEntity.ok()
            .header("X-Total-Count", String.valueOf(repository.findAll().size()))
            .body(tasks);
    }

    @GetMapping("/{id}")
    public TaskDTO show(@PathVariable Long id) {

        var model = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(String.format("Task with id %d not found", id)));

        return mapper.map(model);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDTO create(@Valid @RequestBody TaskCreateDTO dto) {
        var model = mapper.map(dto);
        repository.save(model);
        return mapper.map(model);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDTO updateById(@Valid @RequestBody TaskUpdateDTO dto, @PathVariable Long id) {

        var model = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(String.format("Task with id %d not found", id)));

        mapper.update(dto, model);
        repository.save(model);
        return mapper.map(model);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroyById(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
