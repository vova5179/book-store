package mate.academy.booksstore.service;

import java.util.List;
import mate.academy.booksstore.dto.CategoryDto;
import mate.academy.booksstore.dto.CreateCategoryRequestDto;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto getById(Long id);

    CategoryDto save(CreateCategoryRequestDto requestDto);

    CategoryDto update(Long id, CreateCategoryRequestDto requestDto);

    void deleteById(Long id);
}
