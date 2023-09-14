package mate.academy.booksstore.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.booksstore.dto.UserLoginRequestDto;
import mate.academy.booksstore.dto.UserLoginResponseDto;
import mate.academy.booksstore.dto.UserRegistrationRequestDto;
import mate.academy.booksstore.dto.UserResponseDto;
import mate.academy.booksstore.exception.RegistrationException;
import mate.academy.booksstore.security.AuthenticationService;
import mate.academy.booksstore.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto request)
            throws RegistrationException {
        return userService.register(request);
    }

    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody UserLoginRequestDto request) {
        return authenticationService.authenticate(request);
    }
}
