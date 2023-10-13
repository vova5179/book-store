package mate.academy.booksstore.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.booksstore.dto.OrderDto;
import mate.academy.booksstore.dto.OrderItemDto;
import mate.academy.booksstore.dto.OrderRequestDto;
import mate.academy.booksstore.dto.OrderUpdateRequestDto;
import mate.academy.booksstore.exception.EntityNotFoundException;
import mate.academy.booksstore.mapper.OrderItemMapper;
import mate.academy.booksstore.mapper.OrderMapper;
import mate.academy.booksstore.model.CartItem;
import mate.academy.booksstore.model.Order;
import mate.academy.booksstore.model.OrderItem;
import mate.academy.booksstore.model.ShoppingCart;
import mate.academy.booksstore.model.Status;
import mate.academy.booksstore.repository.OrderRepository;
import mate.academy.booksstore.repository.ShoppingCartRepository;
import mate.academy.booksstore.service.OrderService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderRepository orderRepository;

    @Override
    public OrderDto createOrder(OrderRequestDto requestDto, Long userId) {
        Order order = new Order();
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId).get();
        order.setUser(cart.getUser());
        order.setStatus(Status.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(requestDto.getShippingAddress());
        BigDecimal total = cart.getCartItems().stream()
                .map(c -> c.getBook().getPrice().multiply(BigDecimal.valueOf(c.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotal(total);
        Set<OrderItem> orderItems = cart.getCartItems().stream()
                .map(c -> getOrderItem(c, order))
                .collect(Collectors.toSet());
        order.setOrderItems(orderItems);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public List<OrderDto> getAllOrders(Long userId) {
        return orderRepository.findAllByUserId(userId).stream().map(orderMapper::toDto).toList();
    }

    @Override
    public OrderDto updateOrder(OrderUpdateRequestDto requestDto, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()
                -> new EntityNotFoundException("Can't find order with id " + orderId));
        order.setStatus(Status.valueOf(requestDto.getStatus()));
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public Set<OrderItemDto> getAllItemsByOrderId(Long orderId, Long userId)
                                                  throws IllegalAccessException {
        Order order = orderRepository.findById(orderId).orElseThrow(()
                -> new EntityNotFoundException("Can't find order with id " + orderId));
        if (order.getUser().getId() != userId) {
            throw new IllegalAccessException("You can't see another user's order");
        }
        return orderMapper.toDto(order).getOrderItems();
    }

    @Override
    public OrderItemDto findByOrderIdAndItemId(Long orderId, Long itemId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()
                -> new EntityNotFoundException("Can't find order with id " + orderId));
        OrderItem orderItem = order.getOrderItems().stream()
                .filter(item -> item.getId() == itemId)
                .findFirst().orElseThrow(()
                        -> new EntityNotFoundException("Can't find item with id " + itemId));
        return orderItemMapper.toDto(orderItem);
    }

    private OrderItem getOrderItem(CartItem cartItem, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setBook(cartItem.getBook());
        orderItem.setPrice(cartItem.getBook().getPrice());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setOrder(order);
        return orderItem;
    }
}
