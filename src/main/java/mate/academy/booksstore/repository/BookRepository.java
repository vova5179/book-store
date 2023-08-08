package mate.academy.booksstore.repository;

import java.util.List;
import java.util.Optional;
import mate.academy.booksstore.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Book save(Book product);

    List<Book> findAll();

    Optional<Book> findById(Long id);
}
