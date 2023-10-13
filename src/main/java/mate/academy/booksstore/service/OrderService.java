package mate.academy.booksstore.service;

import java.util.List;
import java.util.Set;
import mate.academy.booksstore.dto.OrderDto;
import mate.academy.booksstore.dto.OrderItemDto;
import mate.academy.booksstore.dto.OrderRequestDto;
import mate.academy.booksstore.dto.OrderUpdateRequestDto;

public interface OrderService {
    OrderDto createOrder(OrderRequestDto requestDto, Long userId);

    List<OrderDto> getAllOrders(Long userId);

    OrderDto updateOrder(OrderUpdateRequestDto requestDto, Long orderId);

    Set<OrderItemDto> getAllItemsByOrderId(Long orderId, Long userId) throws IllegalAccessException;

    OrderItemDto findByOrderIdAndItemId(Long orderId, Long itemsId);
}
