package hexlet.code.service.impl;

import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusDTO;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskStatusServiceImpl implements hexlet.code.service.TaskStatusService {

    private final TaskStatusRepository repository;
    private final TaskStatusMapper mapper;

    @Override
    public List<TaskStatusDTO> findAll() {
        return repository.findAll().stream()
            .map(mapper::map)
            .toList();
    }

    @Override
    public TaskStatusDTO findById(Long id) {

        var model = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(String.format("Task status with id %d not found", id)));

        return mapper.map(model);
    }

    @Override
    public TaskStatusDTO create(TaskStatusCreateDTO dto) {
        var model = mapper.map(dto);
        repository.save(model);
        return mapper.map(model);
    }

    @Override
    public TaskStatusDTO updateById(TaskStatusUpdateDTO dto, Long id) {

        var model = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(String.format("Task status with id %d not found", id)));

        mapper.update(dto, model);
        repository.save(model);
        return mapper.map(model);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
