package mate.academy.booksstore.service.impl;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.booksstore.dto.UserRegistrationRequestDto;
import mate.academy.booksstore.dto.UserResponseDto;
import mate.academy.booksstore.exception.RegistrationException;
import mate.academy.booksstore.mapper.UserMapper;
import mate.academy.booksstore.model.Role;
import mate.academy.booksstore.model.RoleName;
import mate.academy.booksstore.model.User;
import mate.academy.booksstore.repository.RoleRepository;
import mate.academy.booksstore.repository.UserRepository;
import mate.academy.booksstore.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("Unable to complete registration");
        }

        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        user.setShippingAddress(requestDto.getShippingAddress());

        if (requestDto.getEmail().contains("admin")) {
            Role roleAdmin = roleRepository.findRoleByName(RoleName.ADMIN);
            user.setRoles(Set.of(roleAdmin));
        } else {
            Role roleUser = roleRepository.findRoleByName(RoleName.USER);
            user.setRoles((Set.of(roleUser)));
        }
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}
