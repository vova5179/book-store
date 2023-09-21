package mate.academy.booksstore.mapper;

import mate.academy.booksstore.config.MapperConfig;
import mate.academy.booksstore.dto.CategoryDto;
import mate.academy.booksstore.dto.CreateCategoryRequestDto;
import mate.academy.booksstore.model.Category;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toModel(CreateCategoryRequestDto requestDto);
}