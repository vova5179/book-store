package mate.academy.booksstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import mate.academy.booksstore.dto.BookDto;
import mate.academy.booksstore.dto.CreateBookRequestDto;
import mate.academy.booksstore.mapper.BookMapper;
import mate.academy.booksstore.service.BookService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Books management", description = "Endpoints for managing books")
@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService, BookMapper bookMapper) {
        this.bookService = bookService;
    }

    @GetMapping()
    @Operation(summary = "Get all books", description = "Get all books")
    public List<BookDto> getAll(@RequestParam(defaultValue = "5")
                                @Parameter(name = "Default value is 5",
                                description = "Default value is 5") int size,
                                Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a book by id", description = "Get a book by id")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.findBookById(id);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new book", description = "Create a new book")
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto requestDto) {
        return bookService.save(requestDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a book by id", description = "Update a book by id")
    public BookDto updateBook(@PathVariable Long id,
                              @RequestBody @Valid CreateBookRequestDto requestDto) {
        return bookService.update(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book by id", description = "Delete a book by id")
    public void delete(@PathVariable Long id) {
        bookService.delete(id);
    }
}
