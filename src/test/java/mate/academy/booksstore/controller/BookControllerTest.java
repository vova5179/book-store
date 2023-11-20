package mate.academy.booksstore.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.booksstore.dto.BookDto;
import mate.academy.booksstore.dto.CategoryDto;
import mate.academy.booksstore.dto.CreateBookRequestDto;
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
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    private static final Long VALID_ID = 1L;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
        }
    }

    @WithMockUser()
    @Test
    @DisplayName("Get all books")
    void getAll_givenBooksCatalog_returnAllBook() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto[] actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), BookDto[].class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(2, actual.length);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create a new book")
    void createBook_validRequestDto_success() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Inferno")
                .setAuthor("Dan Brown")
                .setPrice(BigDecimal.valueOf(7.99))
                .setIsbn("15444B777")
                .setDescription("Bestseller")
                .setCoverImage("http://example.com/cover2.jpg")
                .setCategoryIds(Set.of(VALID_ID));

        CreateBookRequestDto expected = new CreateBookRequestDto();
        expected.setTitle(requestDto.getTitle())
                .setAuthor(requestDto.getAuthor())
                .setPrice(requestDto.getPrice())
                .setIsbn(requestDto.getIsbn())
                .setDescription(requestDto.getDescription())
                .setCoverImage(requestDto.getCoverImage())
                .setCategoryIds(requestDto.getCategoryIds());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/api/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        BookDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), BookDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser()
    @Test
    @DisplayName("Get a book by id")
    public void getBookById_withValidBookId_returnBookDto() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/books/{id}", VALID_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto expected = new BookDto();
        expected.setTitle("title1")
                .setAuthor("author1")
                .setIsbn("1234567890")
                .setPrice(BigDecimal.valueOf(9.99))
                .setDescription("description1")
                .setCoverImage("cover-image1")
                .setCategoryIds(Set.of(VALID_ID));

        BookDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), BookDto.class);

        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser()
    @Test
    @DisplayName("Verify the status Not Found was returned when book id doesn't exist")
    public void getCategoryById_withInvalidIdCategory_returnStatusNotFound() throws Exception {
        Long invalidId = 10L;
        mockMvc.perform(get("/api/books/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update a book with valid book id")
    void updateBook_validRequestDtoAndId_success() throws Exception {
        CreateBookRequestDto updatedRequestDto = new CreateBookRequestDto();
        updatedRequestDto.setTitle("Inferno")
                .setAuthor("Dan Brown")
                .setPrice(BigDecimal.valueOf(7.99))
                .setIsbn("15444B777")
                .setDescription("Bestseller")
                .setCoverImage("http://example.com/cover2.jpg")
                .setCategoryIds(Set.of(VALID_ID));

        CreateBookRequestDto expected = new CreateBookRequestDto();
        expected.setTitle(updatedRequestDto.getTitle())
                .setAuthor(updatedRequestDto.getAuthor())
                .setPrice(updatedRequestDto.getPrice())
                .setIsbn(updatedRequestDto.getIsbn())
                .setDescription(updatedRequestDto.getDescription())
                .setCoverImage(updatedRequestDto.getCoverImage())
                .setCategoryIds(updatedRequestDto.getCategoryIds());

        String jsonRequest = objectMapper.writeValueAsString(updatedRequestDto);

        MvcResult result = mockMvc.perform(put("/api/books/{id}", VALID_ID)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), CategoryDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Delete a book with valid book id")
    void delete_validId_success() throws Exception {
        mockMvc.perform(delete("/api/books/{id}", VALID_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }
}
