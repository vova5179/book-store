package mate.academy.booksstore.mapper;

import mate.academy.booksstore.config.MapperConfig;
import mate.academy.booksstore.dto.CartItemDto;
import mate.academy.booksstore.dto.CartItemRequestDto;
import mate.academy.booksstore.model.Book;
import mate.academy.booksstore.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    CartItemDto toDto(CartItem cartItem);

    @Mapping(source = "requestDto.bookId", target = "book", qualifiedByName = "bookFromId")
    CartItem toModel(CartItemRequestDto requestDto);

    @Named("bookFromId")
    default Book bookFromId(Long id) {
        Book book = new Book();
        book.setId(id);
        return book;
    }
}
