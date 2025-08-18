package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {

    @Autowired
    private LabelRepository repository;

    @Autowired
    private LabelMapper mapper;

    public List<LabelDTO> findAll() {
        return repository.findAll().stream()
            .map(mapper::map)
            .toList();
    }

    public LabelDTO findById(Long id) {

        var model = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(String.format("Label with id %d not found", id)));

        return mapper.map(model);
    }

    public LabelDTO create(LabelCreateDTO dto) {
        var model = mapper.map(dto);
        repository.save(model);
        return mapper.map(model);
    }

    public LabelDTO updateById(LabelUpdateDTO dto, Long id) {

        var model = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(String.format("Label with id %d not found", id)));

        mapper.update(dto, model);
        repository.save(model);
        return mapper.map(model);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
