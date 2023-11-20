package mate.academy.booksstore.repository;

import java.util.List;
import mate.academy.booksstore.model.Book;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Find all books by category id")
    @Sql(scripts =
            { "classpath:database/books/add-two-books-to-books-table.sql",
                    "classpath:database/books/add-two-categories-to-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts =
            { "classpath:database/books/remove-all-books-from-books-table.sql",
              "classpath:database/books/remove-all-from-books-categories-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findAllByCategoriesId_withValidCategoryId_returnListBooks() {
        List<Book> expectedList = bookRepository.findAllByCategoriesId(1L);

        Assertions.assertEquals(1, expectedList.size());
    }
}
