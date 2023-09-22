package mate.academy.booksstore.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.booksstore.config.MapperConfig;
import mate.academy.booksstore.dto.BookDto;
import mate.academy.booksstore.dto.BookDtoWithoutCategoryIds;
import mate.academy.booksstore.dto.CreateBookRequestDto;
import mate.academy.booksstore.model.Book;
import mate.academy.booksstore.model.Category;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {

    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @AfterMapping
    default void setCategoryIdsToBookDto(@MappingTarget BookDto bookDto, Book book) {
        Set<Long> categoryIds = book.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
        bookDto.setCategoryIds(categoryIds);
    }

    @AfterMapping
    default void setCategoriesFromRequestDto(@MappingTarget Book book,
                                             CreateBookRequestDto requestDto) {
        Set<Category> categories = requestDto.getCategoryIds().stream()
                .map(Category::new)
                .collect(Collectors.toSet());
        book.setCategories(categories);
    }
}
