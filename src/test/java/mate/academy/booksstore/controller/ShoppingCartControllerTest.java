package mate.academy.booksstore.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.booksstore.dto.CartItemRequestDto;
import mate.academy.booksstore.dto.CartItemUpdateRequestDto;
import mate.academy.booksstore.dto.ShoppingCartDto;
import mate.academy.booksstore.security.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShoppingCartControllerTest {
    private static final Long VALID_ID = 1L;

    private static final Long INVALID_ID = Long.MAX_VALUE;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    public void beforeEach(@Autowired DataSource dataSource,
                           @Autowired WebApplicationContext context)
            throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/books/add-two-books-to-books-table.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/books/add-two-categories-to-table.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/books/add-user-to-users-table.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/books/add-shopping-cart-with-"
                            + "two-cart-items-to-shopping-carts-table.sql"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void afterEach(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(
                    "database/books/remove-all-books-from-books-table.sql"));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(
                    "database/books/remove-all-from-books-categories-table.sql"));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(
                    "database/books/remove-all-from-shopping-cart-table.sql"));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(
                    "database/books/remove-all-from-users-table.sql"));
        }

    }

    @Test
    @WithMockUser()
    @DisplayName("Get all cart items in shopping cart")
    public void getShoppingCart_givenUserId_returnShoppingCartDto() throws Exception {
        String token = jwtUtil.generateToken("admin@example.com");
        MvcResult result = mockMvc.perform(get("/api/cart")
                        .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk())
                        .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), ShoppingCartDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(2, actual.getCartItems().size());
    }

    @Test
    @WithMockUser()
    @DisplayName("Add book to shopping cart")
    public void addBookToShoppingCart_givenCartItemRequestDto_success() throws Exception {
        CartItemRequestDto cartItemRequestDto = new CartItemRequestDto();
        cartItemRequestDto.setBookId(2L);
        cartItemRequestDto.setQuantity(7);

        String jsonRequest = objectMapper.writeValueAsString(cartItemRequestDto);
        String token = jwtUtil.generateToken("admin@example.com");

        MvcResult result = mockMvc.perform(post("/api/cart")
                        .header("Authorization", "Bearer " + token)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), ShoppingCartDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(3, actual.getCartItems().size());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Update quantity of books in shopping cart")
    public void updateShoppingCart_updateCartItemRequestDtoValidId_success() throws Exception {
        int actualQuantity = 15;
        CartItemUpdateRequestDto updateRequestDto = new CartItemUpdateRequestDto();
        updateRequestDto.setQuantity(actualQuantity);

        String jsonRequest = objectMapper.writeValueAsString(updateRequestDto);
        String token = jwtUtil.generateToken("admin@example.com");

        MvcResult result = mockMvc.perform(put("/api/cart/cart-items/{id}", VALID_ID)
                        .header("Authorization", "Bearer " + token)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartDto shoppingCartDto = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), ShoppingCartDto.class);
        int expectedQuantity = shoppingCartDto.getCartItems().stream()
                .filter(c -> c.getId() == VALID_ID)
                .findFirst().get().getQuantity();
        Assertions.assertEquals(expectedQuantity, actualQuantity);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Update quantity of books in shopping cart with invalid cart item id")
    public void updateShoppingCart_updateCartItemRequestDtoInvalidId_fail() throws Exception {
        int actualQuantity = 15;
        CartItemUpdateRequestDto updateRequestDto = new CartItemUpdateRequestDto();
        updateRequestDto.setQuantity(actualQuantity);

        String jsonRequest = objectMapper.writeValueAsString(updateRequestDto);
        String token = jwtUtil.generateToken("admin@example.com");
        mockMvc.perform(put("/api/cart/cart-items/{id}", INVALID_ID)
                        .header("Authorization", "Bearer " + token)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Delete book from shopping cart")
    public void removeCartItem_validCartItemId_success() throws Exception {
        String token = jwtUtil.generateToken("admin@example.com");

        mockMvc.perform(delete("/api/cart/cart-items/{id}", VALID_ID)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Delete book from shopping cart with invalid cart item id")
    public void removeCartItem_invalidCartItemId_fail() throws Exception {
        String token = jwtUtil.generateToken("admin@example.com");

        mockMvc.perform(delete("/api/cart/cart-items/{id}", INVALID_ID)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }
}
