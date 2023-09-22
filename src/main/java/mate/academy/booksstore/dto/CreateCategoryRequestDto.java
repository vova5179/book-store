package mate.academy.booksstore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCategoryRequestDto {

    @NotBlank
    private String name;

    @NotBlank
    private String description;
}
