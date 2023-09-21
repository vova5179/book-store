package mate.academy.booksstore.mapper;

import mate.academy.booksstore.config.MapperConfig;
import mate.academy.booksstore.dto.UserRegistrationRequestDto;
import mate.academy.booksstore.dto.UserResponseDto;
import mate.academy.booksstore.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto requestDto);
}
