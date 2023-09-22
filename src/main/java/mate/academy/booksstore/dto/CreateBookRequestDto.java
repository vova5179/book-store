package mate.academy.booksstore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;

@Data
public class CreateBookRequestDto {
    @NotNull
    private String title;
    @NotNull
    private String author;
    @NotNull
    private String isbn;
    @Min(0)
    private BigDecimal price;
    @Size(max = 200)
    private String description;
    private String coverImage;
    private Set<Long> categoryIds;
}
