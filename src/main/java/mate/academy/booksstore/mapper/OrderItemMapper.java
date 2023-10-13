package mate.academy.booksstore.mapper;

import mate.academy.booksstore.config.MapperConfig;
import mate.academy.booksstore.dto.OrderItemDto;
import mate.academy.booksstore.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {

    @Mapping(source = "book.id", target = "bookId")
    OrderItemDto toDto(OrderItem orderItem);
}
