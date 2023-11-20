package mate.academy.booksstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import mate.academy.booksstore.dto.CartItemDto;
import mate.academy.booksstore.dto.CartItemRequestDto;
import mate.academy.booksstore.dto.CartItemUpdateRequestDto;
import mate.academy.booksstore.dto.ShoppingCartDto;
import mate.academy.booksstore.exception.EntityNotFoundException;
import mate.academy.booksstore.mapper.CartItemMapper;
import mate.academy.booksstore.mapper.ShoppingCartMapper;
import mate.academy.booksstore.model.Book;
import mate.academy.booksstore.model.CartItem;
import mate.academy.booksstore.model.Category;
import mate.academy.booksstore.model.Role;
import mate.academy.booksstore.model.RoleName;
import mate.academy.booksstore.model.ShoppingCart;
import mate.academy.booksstore.model.User;
import mate.academy.booksstore.repository.BookRepository;
import mate.academy.booksstore.repository.CartItemsRepository;
import mate.academy.booksstore.repository.ShoppingCartRepository;
import mate.academy.booksstore.repository.UserRepository;
import mate.academy.booksstore.service.impl.ShoppingCartServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {
    private static final Long EXIST_ID = 1L;

    private static final Long INVALID_ID = Long.MAX_VALUE;

    @Mock
    private CartItemMapper cartItemMapper;

    @Mock
    private CartItemsRepository cartItemsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private ShoppingCartMapper shoppingCartMapper;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    private ShoppingCart shoppingCart;

    private ShoppingCartDto shoppingCartDto;

    private CartItemRequestDto cartItemRequestDto;

    private CartItem cartItem;

    private CartItemDto cartItemDto;

    private Book book;

    private User user;

    @BeforeEach
    public void setup() {
        Category category = new Category();
        category.setId(EXIST_ID);
        category.setName("Thriller");
        category.setDescription("Thriller book");
        book = new Book();
        book.setId(EXIST_ID)
                .setTitle("Inferno")
                .setAuthor("Dan Brown")
                .setPrice(BigDecimal.valueOf(7.99))
                .setIsbn("15444B777")
                .setDescription("Bestseller")
                .setCoverImage("http://example.com/cover2.jpg")
                .setCategories(Set.of(category));
        Role roleAdmin = new Role();
        roleAdmin.setName(RoleName.ADMIN);
        user = new User();
        user.setId(EXIST_ID)
                .setEmail("email@example.com")
                .setPassword("password")
                .setFirstName("Bob")
                .setLastName("Bobs")
                .setShippingAddress("Shipping address")
                .setRoles(Set.of(roleAdmin));
        shoppingCart = new ShoppingCart();
        cartItem = new CartItem();
        cartItem.setId(EXIST_ID);
        cartItem.setBook(book);
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setQuantity(2);
        shoppingCart.setId(EXIST_ID);
        shoppingCart.setCartItems(Set.of(cartItem));
        shoppingCart.setUser(user);

        cartItemDto = new CartItemDto();
        cartItemDto.setId(EXIST_ID);
        cartItemDto.setQuantity(cartItem.getQuantity());
        cartItemDto.setBookId(book.getId());
        cartItemDto.setBookTitle(book.getTitle());

        cartItemRequestDto = new CartItemRequestDto();
        cartItemRequestDto.setBookId(book.getId());
        cartItemRequestDto.setQuantity(cartItem.getQuantity());

        shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setId(EXIST_ID);
        shoppingCartDto.setCartItems(Set.of(cartItemDto));
        shoppingCartDto.setUserId(user.getId());

        shoppingCartService = new ShoppingCartServiceImpl(shoppingCartRepository,
                shoppingCartMapper, cartItemMapper,
                cartItemsRepository, userRepository, bookRepository);
    }

    @Test
    @DisplayName("Verify addToCart method works")
    public void addToCart_validCartItemRequestDto_success() {
        Mockito.when(bookRepository.findById(EXIST_ID)).thenReturn(Optional.ofNullable(book));
        Mockito.when(cartItemMapper.toModel(cartItemRequestDto)).thenReturn(cartItem);
        Mockito.when(shoppingCartRepository.save(shoppingCart)).thenReturn(shoppingCart);
        Mockito.when(shoppingCartRepository.findByUserId(EXIST_ID))
                .thenReturn(Optional.ofNullable(shoppingCart));
        Mockito.when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartDto);

        ShoppingCartDto expected = shoppingCartService.addToCart(cartItemRequestDto, EXIST_ID);
        EqualsBuilder.reflectionEquals(expected, shoppingCartDto, "id");
    }

    @Test
    @DisplayName("Verify getShoppingCart method works")
    public void getShoppingCart_validUserId_returnShoppingCartDto() {
        Mockito.when(shoppingCartRepository.findByUserId(EXIST_ID))
                .thenReturn(Optional.ofNullable(shoppingCart));
        Mockito.when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartDto);

        ShoppingCartDto expected = shoppingCartService.getShoppingCart(EXIST_ID);
        EqualsBuilder.reflectionEquals(expected, shoppingCartDto, "id");
    }

    @Test
    @DisplayName("Verify exception was return with non-existent user id")
    public void getShoppingCart_invalidUserId_returnThrowException() {
        Mockito.when(shoppingCartRepository.findByUserId(INVALID_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.getShoppingCart(INVALID_ID));

        String expected = "Can't find user with id " + INVALID_ID;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify updateShoppingCart method works")
    public void updateShoppingCart_validUpdateCartItemRequestDto_success() {
        CartItemUpdateRequestDto cartItemUpdatedRequestDto = new CartItemUpdateRequestDto();
        cartItemUpdatedRequestDto.setQuantity(10);

        ShoppingCart updatedShoppingCart = shoppingCart;
        CartItem updatedCartItem = cartItem;
        updatedCartItem.setShoppingCart(updatedShoppingCart);
        updatedCartItem.setQuantity(cartItemUpdatedRequestDto.getQuantity());
        updatedShoppingCart.setCartItems(Set.of(updatedCartItem));

        CartItemDto updatedCarItemDto = cartItemDto;
        updatedCarItemDto.setQuantity(updatedCartItem.getQuantity());

        ShoppingCartDto actual = new ShoppingCartDto();
        actual.setId(EXIST_ID);
        actual.setCartItems(Set.of(updatedCarItemDto));
        actual.setUserId(updatedShoppingCart.getUser().getId());

        Mockito.when(shoppingCartRepository.findByUserId(EXIST_ID))
                .thenReturn(Optional.of(updatedShoppingCart));
        Mockito.when(shoppingCartMapper.toDto(updatedShoppingCart)).thenReturn(actual);

        ShoppingCartDto expected = shoppingCartService.updateShoppingCart(cartItem.getId(),
                cartItemUpdatedRequestDto, user.getId());

        Assertions.assertNotNull(expected);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify removeCartItem method works")
    public void removeCartItem_validUserIdAndCartId_returnShoppingCartDto() {
        ShoppingCart removedCartItem = shoppingCart;
        removedCartItem.setCartItems(Set.of());
        ShoppingCartDto actual = shoppingCartDto;
        actual.setCartItems(Set.of());

        Mockito.when(cartItemsRepository.existsById(EXIST_ID)).thenReturn(true);
        Mockito.when(shoppingCartRepository.findByUserId(EXIST_ID))
                .thenReturn(Optional.of(removedCartItem));
        Mockito.when(shoppingCartMapper.toDto(removedCartItem)).thenReturn(actual);

        ShoppingCartDto expected = shoppingCartService.removeCartItem(cartItem.getId(),
                user.getId());

        EqualsBuilder.reflectionEquals(expected, actual);
    }
}
