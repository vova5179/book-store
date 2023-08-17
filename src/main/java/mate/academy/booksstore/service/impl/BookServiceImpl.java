package mate.academy.booksstore.service.impl;

import java.util.List;
import mate.academy.booksstore.dto.BookDto;
import mate.academy.booksstore.dto.CreateBookRequestDto;
import mate.academy.booksstore.exception.EntityNotFoundException;
import mate.academy.booksstore.mapper.BookMapper;
import mate.academy.booksstore.model.Book;
import mate.academy.booksstore.repository.BookRepository;
import mate.academy.booksstore.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> findAll() {
        List<Book> bookList = bookRepository.findAll();
        return bookList.stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto findBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Can't find book with id " + id));
        return bookMapper.toDto(book);
    }

    @Override
    public BookDto update(Long id, CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        book.setId(id);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new IllegalArgumentException("There's no book with id: " + id);
        }
        bookRepository.deleteById(id);
    }
}
