package mate.academy.booksstore.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.booksstore.model.Category;
import mate.academy.booksstore.repository.CategoryRepository;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class MapperUtil {
    private final CategoryRepository categoryRepository;

    public MapperUtil(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Named("getCategoryById")
    public Set<Category> getCategoryById(Set<Long> ids) {
        return ids.stream()
                .map(categoryRepository::getReferenceById)
                .collect(Collectors.toSet());
    }
}
