package hexlet.code.mapper;

import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.model.User;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(
    uses = { JsonNullableMapper.class },
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Mapping(source = "password", target = "passwordDigest")
    public abstract User map(UserCreateDTO dto);

    public abstract UserDTO map(User model);
    public abstract User map(UserDTO dto);

    @Mapping(source = "password", target = "passwordDigest")
    public abstract void update(UserUpdateDTO dto, @MappingTarget User model);

    @BeforeMapping
    public void encryptPassword(UserCreateDTO dto) {
        var password = dto.getPassword();
        dto.setPassword(passwordEncoder.encode(password));
    }

    @BeforeMapping
    public void encryptPassword(UserUpdateDTO dto) {
        var password = dto.getPassword().get();
        var encryptedPassword = passwordEncoder.encode(password);
        dto.setPassword(JsonNullable.of(encryptedPassword));
    }
}
