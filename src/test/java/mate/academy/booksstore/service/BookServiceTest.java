package mate.academy.booksstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import mate.academy.booksstore.dto.BookDto;
import mate.academy.booksstore.dto.BookDtoWithoutCategoryIds;
import mate.academy.booksstore.dto.CreateBookRequestDto;
import mate.academy.booksstore.exception.EntityNotFoundException;
import mate.academy.booksstore.mapper.BookMapper;
import mate.academy.booksstore.model.Book;
import mate.academy.booksstore.model.Category;
import mate.academy.booksstore.repository.BookRepository;
import mate.academy.booksstore.service.impl.BookServiceImpl;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    private static final Long EXIST_ID = 1L;

    private static final Long INVALID_ID = Long.MAX_VALUE;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;

    private BookDto bookResponseDto;

    private CreateBookRequestDto bookRequestDto;

    private Category category;

    @BeforeEach
    public void setup() {
        category = new Category();
        category.setId(EXIST_ID);
        category.setName("Thriller");
        category.setDescription("Thriller book");
        book = new Book();
        book.setId(EXIST_ID)
                .setTitle("Inferno")
                .setAuthor("Dan Brown")
                .setPrice(BigDecimal.valueOf(7.99))
                .setIsbn("15444B777")
                .setDescription("Bestseller")
                .setCoverImage("http://example.com/cover2.jpg")
                .setCategories(Set.of(category));
        bookResponseDto = new BookDto();
        bookResponseDto.setId(EXIST_ID)
                .setTitle(book.getTitle())
                .setAuthor(book.getAuthor())
                .setPrice(book.getPrice())
                .setIsbn(book.getIsbn())
                .setDescription(book.getDescription())
                .setCoverImage(book.getCoverImage())
                .setCategoryIds(Set.of(EXIST_ID));
        bookRequestDto = new CreateBookRequestDto();
        bookRequestDto.setTitle(book.getTitle())
                .setAuthor(book.getAuthor())
                .setPrice(book.getPrice())
                .setIsbn(book.getIsbn())
                .setDescription(book.getDescription())
                .setCoverImage(book.getCoverImage())
                .setCategoryIds(Set.of(EXIST_ID));
        bookRequestDto = new CreateBookRequestDto();

        bookService = new BookServiceImpl(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify findAll method works")
    public void findAll_givenPageable_returnListBooksDto() {
        ArrayList<Book> actualList = new ArrayList<>();
        actualList.add(book);

        Mockito.when(bookRepository.findAll()).thenReturn(actualList);
        Mockito.when(bookMapper.toDto(book)).thenReturn(bookResponseDto);

        Pageable pageable = PageRequest.of(0, 5);
        List<BookDto> expectedList = bookService.findAll(pageable);
        assertEquals(1, expectedList.size());
    }

    @Test
    @DisplayName("Verify the correct book was returned when book exist")
    public void findBookById_withValidBookId_returnValidBook() {
        Mockito.when(bookRepository.findById(EXIST_ID)).thenReturn(Optional.of(book));
        Mockito.when(bookMapper.toDto(book)).thenReturn(bookResponseDto);

        String expect = bookService.findBookById(EXIST_ID).getIsbn();
        String actual = bookResponseDto.getIsbn();
        assertEquals(expect, actual);
    }

    @Test
    @DisplayName("Verify the exception was returned when book id doesn't exist")
    public void findBookById_withInvalidCategoryId_returnThrowException() {
        lenient().when(bookRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.findBookById(INVALID_ID));

        String expected = "Can't find book with id " + INVALID_ID;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify save method works")
    public void save_ValidCreateBokRequestDto_returnBookDto() {
        Mockito.when(bookMapper.toModel(bookRequestDto)).thenReturn(book);
        Mockito.when(bookRepository.save(book)).thenReturn(book);
        Mockito.when(bookMapper.toDto(book)).thenReturn(bookResponseDto);

        BookDto expectedResponceDto = bookService.save(bookRequestDto);

        assertTrue(EqualsBuilder.reflectionEquals(expectedResponceDto, bookResponseDto));
    }

    @Test
    @DisplayName("Verify correctly updated book was returned when book exist")
    public void update_withValidBookId_returnBookDto() {
        CreateBookRequestDto updateRequestDto = new CreateBookRequestDto();
        updateRequestDto.setTitle("Update")
                .setAuthor(book.getAuthor())
                .setPrice(book.getPrice())
                .setIsbn(book.getIsbn())
                .setDescription(book.getDescription())
                .setCoverImage(book.getCoverImage())
                .setCategoryIds(Set.of(EXIST_ID));

        Book updateBook = new Book();
        updateBook.setId(EXIST_ID)
                .setTitle(updateRequestDto.getTitle())
                .setAuthor(book.getAuthor())
                .setPrice(book.getPrice())
                .setIsbn(book.getIsbn())
                .setDescription(book.getDescription())
                .setCoverImage(book.getCoverImage())
                .setCategories(Set.of(category));

        BookDto updateResponseDto = new BookDto();
        updateResponseDto.setId(EXIST_ID)
                .setTitle(updateRequestDto.getTitle())
                .setAuthor(book.getAuthor())
                .setPrice(book.getPrice())
                .setIsbn(book.getIsbn())
                .setDescription(book.getDescription())
                .setCoverImage(book.getCoverImage())
                .setCategoryIds(Set.of(EXIST_ID));

        Mockito.when(bookMapper.toModel(updateRequestDto)).thenReturn(updateBook);
        Mockito.when(bookRepository.save(updateBook)).thenReturn(updateBook);
        Mockito.when(bookMapper.toDto(updateBook)).thenReturn(updateResponseDto);

        BookDto expectedResult = bookService.update(EXIST_ID, updateRequestDto);

        assertTrue(EqualsBuilder.reflectionEquals(expectedResult, updateResponseDto));
    }

    @Test
    @DisplayName("Verify exception was returned when update book with non-existent id")
    public void update_withInvalidBookId_returnThrowException() {
        CreateBookRequestDto nonExistentRequestDto = new CreateBookRequestDto();
        nonExistentRequestDto.setTitle("non-existent category")
                .setAuthor(book.getAuthor())
                .setPrice(book.getPrice())
                .setIsbn(book.getIsbn())
                .setDescription(book.getDescription())
                .setCoverImage(book.getCoverImage())
                .setCategoryIds(Set.of(EXIST_ID));

        lenient().when(bookRepository.existsById(INVALID_ID)).thenReturn(false);
        assertThrows(RuntimeException.class,
                () -> bookService.update(INVALID_ID, nonExistentRequestDto));
    }

    @Test
    @DisplayName("Verify delete method works")
    public void delete_withValidCategoryId_removeBook() {
        Mockito.when(bookRepository.existsById(book.getId())).thenReturn(true);

        bookService.delete(book.getId());
        verify(bookRepository).deleteById(book.getId());
    }

    @Test
    @DisplayName("Verify exception was returned when delete book with non-existent id")
    public void delete_withInvalidCategoryId_returnThrowException() {
        Mockito.when(bookRepository.existsById(INVALID_ID)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> bookService.delete(INVALID_ID));
        verify(bookRepository, never()).deleteById(INVALID_ID);
    }

    @Test
    @DisplayName("Verify findAll method works")
    public void findBooksByCategoryId_withValidCategoryId_returnListBooksDtoWithoutCategoryIds() {
        BookDtoWithoutCategoryIds bookDtoWithoutCategoryId = new BookDtoWithoutCategoryIds();
        bookDtoWithoutCategoryId.setId(EXIST_ID)
                .setTitle(book.getTitle())
                .setAuthor(book.getAuthor())
                .setPrice(book.getPrice())
                .setIsbn(book.getIsbn())
                .setDescription(book.getDescription())
                .setCoverImage(book.getCoverImage());
        ArrayList<Book> listBooks = new ArrayList<>();
        listBooks.add(book);

        Mockito.when(bookRepository.findAllByCategoriesId(EXIST_ID)).thenReturn(listBooks);
        Mockito.when(bookMapper.toDtoWithoutCategories(book)).thenReturn(bookDtoWithoutCategoryId);

        List<BookDtoWithoutCategoryIds> expectedList = bookService.findBooksByCategoryId(EXIST_ID);
        assertEquals(1, expectedList.size());
    }
}
