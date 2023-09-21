package mate.academy.booksstore.service;

import mate.academy.booksstore.dto.CategoryDto;
import mate.academy.booksstore.dto.UserRegistrationRequestDto;
import mate.academy.booksstore.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto);
}
