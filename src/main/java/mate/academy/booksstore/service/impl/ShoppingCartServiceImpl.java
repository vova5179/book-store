package mate.academy.booksstore.service.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.booksstore.dto.CartItemRequestDto;
import mate.academy.booksstore.dto.CartItemUpdateRequestDto;
import mate.academy.booksstore.dto.ShoppingCartDto;
import mate.academy.booksstore.exception.EntityNotFoundException;
import mate.academy.booksstore.mapper.CartItemMapper;
import mate.academy.booksstore.mapper.ShoppingCartMapper;
import mate.academy.booksstore.model.Book;
import mate.academy.booksstore.model.CartItem;
import mate.academy.booksstore.model.ShoppingCart;
import mate.academy.booksstore.model.User;
import mate.academy.booksstore.repository.BookRepository;
import mate.academy.booksstore.repository.CartItemsRepository;
import mate.academy.booksstore.repository.ShoppingCartRepository;
import mate.academy.booksstore.repository.UserRepository;
import mate.academy.booksstore.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemMapper cartItemMapper;
    private final CartItemsRepository cartItemsRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Override
    public ShoppingCartDto addToCart(CartItemRequestDto requestDto, Long userId) {
        if (!checkExistShoppingCart(userId)) {
            registerNewShoppingCart(userId);
        }
        ShoppingCart shoppingCart = getShoppingCartModel(userId);
        CartItem cartItem = cartItemMapper.toModel(requestDto);
        Long bookId = cartItem.getBook().getId();
        Book book = bookRepository.findById(bookId).orElseThrow(() ->
                new EntityNotFoundException("Can't find book with id" + bookId));
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(book);
        cartItemsRepository.save(cartItem);
        return shoppingCartMapper.toDto(shoppingCartRepository.save(shoppingCart));
    }

    @Override
    public ShoppingCartDto getShoppingCart(Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId).orElseThrow(() ->
                new EntityNotFoundException("Can't find user with id " + userId));
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto updateShoppingCart(Long cartId, CartItemUpdateRequestDto requestDto,
                                              Long userId) {
        Integer quantity = requestDto.getQuantity();
        ShoppingCart shoppingCart = getShoppingCartModel(userId);
        CartItem cartItem = shoppingCart.getCartItems().stream()
                .filter(c -> c.getId() == cartId)
                .findFirst().orElseThrow(() ->
                        new EntityNotFoundException("Can't find book with id " + cartId));
        cartItem.setQuantity(quantity);
        return getShoppingCart(userId);
    }

    @Override
    public ShoppingCartDto removeCartItem(Long cartId, Long userId) {
        if (!cartItemsRepository.existsById(cartId)) {
            throw new EntityNotFoundException("Can't delete cart item by id: " + cartId);
        }
        cartItemsRepository.deleteById(cartId);
        return getShoppingCart(userId);
    }

    private boolean checkExistShoppingCart(Long id) {
        return shoppingCartRepository.findByUserId(id).isPresent();
    }

    private ShoppingCart getShoppingCartModel(Long userId) {
        return shoppingCartRepository.findByUserId(userId).get();
    }

    private void registerNewShoppingCart(Long userId) {
        User user = userRepository.findById(userId).get();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }
}
