package mate.academy.booksstore.service;

import mate.academy.booksstore.dto.UserRegistrationRequestDto;
import mate.academy.booksstore.dto.UserResponseDto;
import mate.academy.booksstore.model.User;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto);

    User getUser();
}
