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
import mate.academy.booksstore.dto.BookDtoWithoutCategoryIds;
import mate.academy.booksstore.dto.CategoryDto;
import mate.academy.booksstore.dto.CreateCategoryRequestDto;
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
class CategoryControllerTest {
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
    @DisplayName("Get all categories")
    void getAll_givenCategoriesCatalog_returnAllCategories() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto[] actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), CategoryDto[].class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(2, actual.length);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create a new category")
    void createCategory_validRequestDto_success() throws Exception {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName("Drama");
        requestDto.setDescription("Drama book");

        CategoryDto expected = new CategoryDto();
        expected.setName(requestDto.getName());
        expected.setDescription(requestDto.getDescription());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/api/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), CategoryDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser()
    @Test
    @DisplayName("Get a category by id")
    public void getCategoryById_withValidCategoryId_returnCategoryDto() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/categories/{id}", VALID_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), CategoryDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals("Fiction", actual.getName());
    }

    @WithMockUser()
    @Test
    @DisplayName("Verify the status Not Found was returned when category id doesn't exist")
    public void getCategoryById_withInvalidIdCategory_returnStatusNotFound() throws Exception {
        Long invalidId = 10L;
        mockMvc.perform(get("/api/categories/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andReturn();
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update a category with valid category id")
    void update_validRequestDtoAndId_success() throws Exception {
        CreateCategoryRequestDto updatedRequestDto = new CreateCategoryRequestDto();
        updatedRequestDto.setName("Drama");
        updatedRequestDto.setDescription("Drama book");
        CategoryDto expected = new CategoryDto();
        expected.setName(updatedRequestDto.getName());
        expected.setDescription(updatedRequestDto.getDescription());

        String jsonRequest = objectMapper.writeValueAsString(updatedRequestDto);

        MvcResult result = mockMvc.perform(put("/api/categories/{id}", VALID_ID)
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
    @DisplayName("Delete a category with valid category id")
    void delete_validId_success() throws Exception {
        mockMvc.perform(delete("/api/categories/{id}", VALID_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn();
    }

    @WithMockUser()
    @Test
    @DisplayName("Get all books by category id")
    void getBooksByCategoryId_givenCategoryId_returnListBookDtoWithoutCategoryIds()
            throws Exception {
        MvcResult result = mockMvc.perform(get("/api/categories/{id}/books", VALID_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDtoWithoutCategoryIds[] actual = objectMapper.readValue(result
                        .getResponse()
                        .getContentAsString(), BookDtoWithoutCategoryIds[].class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(1, actual.length);
    }
}
