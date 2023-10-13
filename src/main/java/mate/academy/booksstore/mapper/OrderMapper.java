package mate.academy.booksstore.mapper;

import mate.academy.booksstore.config.MapperConfig;
import mate.academy.booksstore.dto.OrderDto;
import mate.academy.booksstore.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "orderItems", target = "orderItems")
    OrderDto toDto(Order order);
}
