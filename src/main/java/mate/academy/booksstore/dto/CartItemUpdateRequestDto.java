package mate.academy.booksstore.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CartItemUpdateRequestDto {
    @NotNull
    @Positive
    private Integer quantity;
}
