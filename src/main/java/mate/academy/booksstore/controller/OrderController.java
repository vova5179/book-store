package mate.academy.booksstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.booksstore.dto.OrderDto;
import mate.academy.booksstore.dto.OrderItemDto;
import mate.academy.booksstore.dto.OrderRequestDto;
import mate.academy.booksstore.dto.OrderUpdateRequestDto;
import mate.academy.booksstore.model.User;
import mate.academy.booksstore.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Create an order")
    public OrderDto createOrder(@RequestBody OrderRequestDto requestDto,
                                          Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return orderService.createOrder(requestDto, user.getId());
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Retrieve user's order history")
    public List<OrderDto> getAllOrdersByUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return orderService.getAllOrders(user.getId());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update order status (Admin only).")
    public OrderDto updateOrder(@PathVariable Long id,
                                @RequestBody OrderUpdateRequestDto requestDto) {
        return orderService.updateOrder(requestDto, id);
    }

    @GetMapping("/{orderId}/items")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Retrieve all order items for a specific order")
    public Set<OrderItemDto> getAllItemsByOrderId(@PathVariable Long orderId,
                                       Authentication authentication)
                                       throws IllegalAccessException {
        User user = (User) authentication.getPrincipal();
        return orderService.getAllItemsByOrderId(orderId, user.getId());
    }

    @GetMapping("/{orderId}/items/{itemsId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Retrieve a specific order item within an order")
    public OrderItemDto getByItemId(@PathVariable Long orderId, @PathVariable Long itemsId) {
        return orderService.findByOrderIdAndItemId(orderId, itemsId);
    }
}
