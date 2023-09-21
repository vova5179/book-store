package mate.academy.booksstore.service;

import java.util.List;
import mate.academy.booksstore.dto.BookDto;
import mate.academy.booksstore.dto.BookDtoWithoutCategoryIds;
import mate.academy.booksstore.dto.CreateBookRequestDto;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto bookDto);

    List<BookDto> findAll(Pageable pageable);

    BookDto findBookById(Long id);

    List<BookDtoWithoutCategoryIds> findBooksByCategoryId(Long id);

    BookDto update(Long id, CreateBookRequestDto bookDto);

    void delete(Long id);
}
