package mate.academy.booksstore.service.impl;

import java.util.List;
import mate.academy.booksstore.model.Book;
import mate.academy.booksstore.repository.BookRepository;
import mate.academy.booksstore.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book product) {
        return bookRepository.save(product);
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }
}
