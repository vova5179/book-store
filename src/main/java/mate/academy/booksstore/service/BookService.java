package mate.academy.booksstore.service;

import java.util.List;
import mate.academy.booksstore.model.Book;

public interface BookService {
    Book save(Book product);

    List<Book> findAll();
}
