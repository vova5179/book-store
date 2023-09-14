package mate.academy.booksstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import mate.academy.booksstore.lib.FieldsValueMatch;
import mate.academy.booksstore.lib.ValidEmail;

@Data
@FieldsValueMatch(
        field = "password",
        fieldMatch = "repeatPassword",
        message = "Passwords don't match!"
)
public class UserRegistrationRequestDto {
    @ValidEmail
    private String email;

    @Size(min = 8, max = 200)
    private String password;

    private String repeatPassword;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String shippingAddress;
}
