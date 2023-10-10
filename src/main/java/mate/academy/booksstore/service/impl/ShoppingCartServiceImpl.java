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
import mate.academy.booksstore.service.UserService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemMapper cartItemMapper;
    private final CartItemsRepository cartItemsRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final BookRepository bookRepository;

    @Override
    public ShoppingCartDto addCartItemToShoppingCart(CartItemRequestDto requestDto) {
        String email = userService.getUser().getEmail();
        if (!checkExistShoppingCart(email)) {
            registerNewShoppingCart(email);
        }
        ShoppingCart shoppingCart = getShoppingCartModel();
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
    public ShoppingCartDto getShoppingCart() {
        String email = userService.getUser().getEmail();
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserEmail(email).orElseThrow(() ->
                new EntityNotFoundException("Can't find user with email " + email));
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto updateShoppingCart(Long id, CartItemUpdateRequestDto requestDto) {
        Integer quantity = requestDto.getQuantity();
        ShoppingCart shoppingCart = getShoppingCartModel();
        CartItem cartItem = shoppingCart.getCartItems().stream()
                .filter(c -> c.getId() == id)
                .findFirst().orElseThrow(() ->
                        new EntityNotFoundException("Can't find book with id " + id));
        cartItem.setQuantity(quantity);
        return getShoppingCart();
    }

    @Override
    public ShoppingCartDto removeCartItem(Long id) {
        if (!cartItemsRepository.existsById(id)) {
            throw new EntityNotFoundException("Can't delete cart item by id: " + id);
        }
        cartItemsRepository.deleteById(id);
        return getShoppingCart();
    }

    private boolean checkExistShoppingCart(String email) {
        return shoppingCartRepository.findByUserEmail(email).isPresent();
    }

    private ShoppingCart getShoppingCartModel() {
        User user = userService.getUser();
        return shoppingCartRepository.findByUserEmail(user.getEmail()).get();
    }

    private void registerNewShoppingCart(String email) {
        User userByEmail = userRepository.findByEmail(email).get();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(userByEmail);
        shoppingCartRepository.save(shoppingCart);
    }
}
