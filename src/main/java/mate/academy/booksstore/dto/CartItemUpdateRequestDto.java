package mate.academy.booksstore.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CartItemUpdateRequestDto {
    @Min(1)
    private Integer quantity;
}
