package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.dto.task.TaskUpdateDTO;

import java.util.List;

public interface TaskService {
    List<TaskDTO> findAll(TaskParamsDTO params, int pageCount, int pageSize);
    TaskDTO findById(Long id);
    TaskDTO create(TaskCreateDTO dto);
    TaskDTO updateById(TaskUpdateDTO dto, Long id);
    void deleteById(Long id);
}
