package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.specification.TaskSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskService {

    private final TaskRepository repository;
    private final TaskMapper mapper;
    private final TaskSpecification specBuilder;

    public ResponseEntity<List<TaskDTO>> findAll(TaskParamsDTO params, int pageCount, int pageSize) {

        var spec = specBuilder.build(params);
        var pageParams = PageRequest.of(pageCount - 1, pageSize);
        var tasks = repository.findAll(spec, pageParams).map(mapper::map).toList();

        return ResponseEntity.ok()
            .header("X-Total-Count", String.valueOf(repository.findAll().size()))
            .body(tasks);
    }

    public TaskDTO findById(Long id) {

        var model = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(String.format("Task with id %d not found", id)));

        return mapper.map(model);
    }

    public TaskDTO create(TaskCreateDTO dto) {
        var model = mapper.map(dto);
        repository.save(model);
        return mapper.map(model);
    }

    public TaskDTO updateById(TaskUpdateDTO dto, Long id) {

        var model = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(String.format("Task with id %d not found", id)));

        mapper.update(dto, model);
        repository.save(model);
        return mapper.map(model);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
