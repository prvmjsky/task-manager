package hexlet.code.dto.users;

import org.openapitools.jackson.nullable.JsonNullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO {

    @NotNull
    private JsonNullable<String> firstName;

    @NotNull
    private JsonNullable<String> lastName;

    @NotNull
    private JsonNullable<String> email;

    @NotNull
    private JsonNullable<String> password;
}
