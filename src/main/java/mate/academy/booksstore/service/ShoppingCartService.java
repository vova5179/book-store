package mate.academy.booksstore.service;

import mate.academy.booksstore.dto.CartItemRequestDto;
import mate.academy.booksstore.dto.CartItemUpdateRequestDto;
import mate.academy.booksstore.dto.ShoppingCartDto;

public interface ShoppingCartService {

    ShoppingCartDto addCartItemToShoppingCart(CartItemRequestDto requestDto);

    ShoppingCartDto getShoppingCart();

    ShoppingCartDto updateShoppingCart(Long id, CartItemUpdateRequestDto requestDto);

    ShoppingCartDto removeCartItem(Long id);
}
