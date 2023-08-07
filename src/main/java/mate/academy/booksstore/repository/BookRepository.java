package mate.academy.booksstore.repository;

import java.util.List;
import mate.academy.booksstore.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    Book save(Book product);

    List<Book> findAll();
}
