package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;

import java.util.List;

public interface LabelService {
    List<LabelDTO> findAll();
    LabelDTO findById(Long id);
    LabelDTO create(LabelCreateDTO dto);
    LabelDTO updateById(LabelUpdateDTO dto, Long id);
    void deleteById(Long id);
}
