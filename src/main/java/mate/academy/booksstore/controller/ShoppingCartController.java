package mate.academy.booksstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import mate.academy.booksstore.dto.CartItemRequestDto;
import mate.academy.booksstore.dto.CartItemUpdateRequestDto;
import mate.academy.booksstore.dto.ShoppingCartDto;
import mate.academy.booksstore.service.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Add book to shopping cart")
    public ShoppingCartDto addBookToShoppingCart(@RequestBody @Valid
                                                     CartItemRequestDto requestDto) {
        return shoppingCartService.addCartItemToShoppingCart(requestDto);
    }

    @GetMapping()
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get all cart items in shopping cart")
    public ShoppingCartDto getShoppingCart() {
        return shoppingCartService.getShoppingCart();
    }

    @PutMapping("/cart-items/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Update quantity of book in shopping cart")
    public ShoppingCartDto updateShoppingCart(@PathVariable Long id,
                                              @RequestBody
                                              @Valid CartItemUpdateRequestDto requestDto) {
        return shoppingCartService.updateShoppingCart(id, requestDto);
    }

    @DeleteMapping("/cart-items/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Delete book from shopping cart")
    public ShoppingCartDto removeCartItem(@PathVariable Long id) {
        return shoppingCartService.removeCartItem(id);
    }
}
