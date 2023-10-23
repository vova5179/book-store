package mate.academy.booksstore.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderUpdateRequestDto {
    @NotNull
    private String status;
}
