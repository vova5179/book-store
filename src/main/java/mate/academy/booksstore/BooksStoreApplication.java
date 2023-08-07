package mate.academy.booksstore;

import java.math.BigDecimal;
import mate.academy.booksstore.model.Book;
import mate.academy.booksstore.service.BookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BooksStoreApplication {
    private final BookService bookService;

    public BooksStoreApplication(BookService bookService) {
        this.bookService = bookService;
    }

    public static void main(String[] args) {

        SpringApplication.run(BooksStoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book book = new Book();
            book.setIsbn("9781234567897");
            book.setTitle("Sample Book 1");
            book.setAuthor("Author A");
            book.setPrice(BigDecimal.valueOf(19.99));
            book.setDescription("This is a sample book description.");
            book.setCoverImage("coverImage\": \"http://example.com/cover1.jpg");
            bookService.save(book);
            System.out.println(bookService.findAll());
        };
    }
}
