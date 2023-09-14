package mate.academy.booksstore.security;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mate.academy.booksstore.model.Role;
import mate.academy.booksstore.model.User;
import mate.academy.booksstore.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        org.springframework.security.core.userdetails.User.UserBuilder builder;
        if (userOptional.isPresent()) {
            builder = org.springframework.security.core.userdetails.User.withUsername(email);
            builder.password(userOptional.get().getPassword());
            builder.roles(userOptional.get().getRoles().stream()
                    .map(Role::getName)
                    .map(Enum::toString)
                    .toArray(String[]::new));
            builder.accountExpired(false);
            builder.disabled(userOptional.get().isDeleted());
            return builder.build();
        }
        throw new UsernameNotFoundException("User not found");

    }
}
