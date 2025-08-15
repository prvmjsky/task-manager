package hexlet.code.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class TaskCreateDTO {
    private Integer index;
    private Long assigneeId;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private String status;
}
