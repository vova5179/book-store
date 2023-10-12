package mate.academy.booksstore.service;

import java.util.List;
import mate.academy.booksstore.dto.CartItemDto;
import mate.academy.booksstore.dto.CartItemRequestDto;

public interface CartItemService {

    List<CartItemDto> findAll();

    CartItemDto getById(Long id);

    CartItemDto save(CartItemRequestDto requestDto);

    CartItemDto update(Long id, CartItemRequestDto requestDto);

    void deleteById(Long id);
}
