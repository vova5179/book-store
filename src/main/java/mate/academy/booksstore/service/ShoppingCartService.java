package mate.academy.booksstore.service;

import mate.academy.booksstore.dto.CartItemRequestDto;
import mate.academy.booksstore.dto.CartItemUpdateRequestDto;
import mate.academy.booksstore.dto.ShoppingCartDto;

public interface ShoppingCartService {

    ShoppingCartDto addToCart(CartItemRequestDto requestDto, Long userId);

    ShoppingCartDto getShoppingCart(Long userId);

    ShoppingCartDto updateShoppingCart(Long cartId,
                                       CartItemUpdateRequestDto requestDto,
                                       Long userId);

    ShoppingCartDto removeCartItem(Long cartId, Long userId);
}
