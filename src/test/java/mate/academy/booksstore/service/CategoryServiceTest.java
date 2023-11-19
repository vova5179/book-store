package mate.academy.booksstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.academy.booksstore.dto.CategoryDto;
import mate.academy.booksstore.dto.CreateCategoryRequestDto;
import mate.academy.booksstore.exception.EntityNotFoundException;
import mate.academy.booksstore.mapper.CategoryMapper;
import mate.academy.booksstore.model.Category;
import mate.academy.booksstore.repository.CategoryRepository;
import mate.academy.booksstore.service.impl.CategoryServiceImpl;
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
class CategoryServiceTest {
    private static final Long EXIST_ID = 1L;

    private static final Long INVALID_ID = Long.MAX_VALUE;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private CreateCategoryRequestDto requestDtoHorror;

    private CreateCategoryRequestDto requestDtoDrama;

    private Category categoryHorror;

    private Category categoryDrama;

    private CategoryDto responseDtoHorror;

    private CategoryDto responseDtoDrama;

    @BeforeEach
    public void setup() {
        requestDtoHorror = new CreateCategoryRequestDto();
        requestDtoHorror.setName("Horror");
        requestDtoHorror.setDescription("Horror book");

        categoryHorror = new Category();
        categoryHorror.setId(EXIST_ID);
        categoryHorror.setName(requestDtoHorror.getName());
        categoryHorror.setDescription(requestDtoHorror.getDescription());

        responseDtoHorror = new CategoryDto();
        responseDtoHorror.setId(EXIST_ID);
        responseDtoHorror.setName(requestDtoHorror.getName());
        responseDtoHorror.setDescription(requestDtoHorror.getDescription());

        requestDtoDrama = new CreateCategoryRequestDto();
        requestDtoDrama.setName("Drama");
        requestDtoDrama.setDescription("Drama book");

        categoryDrama = new Category();
        categoryDrama.setId(2L);
        categoryDrama.setName(categoryDrama.getName());
        categoryDrama.setDescription(categoryDrama.getDescription());

        responseDtoDrama = new CategoryDto();
        responseDtoDrama.setId(categoryDrama.getId());
        responseDtoDrama.setName(requestDtoDrama.getName());
        responseDtoDrama.setDescription(requestDtoDrama.getDescription());

        categoryService = new CategoryServiceImpl(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify findAll method works")
    public void findAll_givenPageable_returnListCategoriesDto() {
        ArrayList<Category> listCategories = new ArrayList<>();
        listCategories.add(categoryDrama);
        listCategories.add(categoryHorror);

        Mockito.when(categoryRepository.findAll()).thenReturn(listCategories);
        Mockito.when(categoryMapper.toDto(categoryDrama)).thenReturn(responseDtoDrama);
        Mockito.when(categoryMapper.toDto(categoryHorror)).thenReturn(responseDtoHorror);

        Pageable pageable = PageRequest.of(0, 5);
        List<CategoryDto> actual = categoryService.findAll(pageable);
        assertEquals(2, actual.size());
    }

    @Test
    @DisplayName("Verify the correct category was returned when category exist")
    public void getById_withValidCategoryId_returnValidCategory() {
        Mockito.when(categoryRepository.findById(EXIST_ID)).thenReturn(Optional.of(categoryHorror));
        Mockito.when(categoryMapper.toDto(categoryHorror)).thenReturn(responseDtoHorror);

        String expect = categoryService.getById(EXIST_ID).getName();
        String actual = responseDtoHorror.getName();
        assertEquals(expect, actual);
    }

    @Test
    @DisplayName("Verify the exception was returned when category id doesn't exist")
    public void getById_withInvalidCategoryId_returnThrowException() {
        lenient().when(categoryRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(INVALID_ID));

        String expected = "Can't find category with id " + INVALID_ID;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify save method works")
    public void save_ValidCreateCategoryRequestDto_returnCategoryDto() {
        Mockito.when(categoryMapper.toModel(requestDtoHorror)).thenReturn(categoryHorror);
        Mockito.when(categoryRepository.save(categoryHorror)).thenReturn(categoryHorror);
        Mockito.when(categoryMapper.toDto(categoryHorror)).thenReturn(responseDtoHorror);

        CategoryDto expectedCategoryDto = categoryService.save(requestDtoHorror);

        assertTrue(EqualsBuilder.reflectionEquals(expectedCategoryDto, responseDtoHorror));
    }

    @Test
    @DisplayName("Verify correctly updated category was returned when category exist")
    public void update_withValidCategoryId_returnCategoryDto() {
        CreateCategoryRequestDto updateRequestDto = new CreateCategoryRequestDto();
        updateRequestDto.setName("update book");
        updateRequestDto.setDescription("update description");

        Category updateCategory = new Category();
        updateCategory.setId(EXIST_ID);
        updateCategory.setName(updateRequestDto.getName());
        updateCategory.setDescription(updateRequestDto.getDescription());

        CategoryDto actualResponseDto = new CategoryDto();
        actualResponseDto.setId(EXIST_ID);
        actualResponseDto.setName(updateRequestDto.getName());
        actualResponseDto.setDescription(updateRequestDto.getDescription());

        Mockito.when(categoryMapper.toModel(updateRequestDto)).thenReturn(updateCategory);
        Mockito.when(categoryRepository.save(updateCategory)).thenReturn(updateCategory);
        Mockito.when(categoryMapper.toDto(updateCategory)).thenReturn(actualResponseDto);

        CategoryDto expectedResponseDto = categoryService.update(EXIST_ID, updateRequestDto);

        assertTrue(EqualsBuilder.reflectionEquals(expectedResponseDto, actualResponseDto));
    }

    @Test
    @DisplayName("Verify exception was returned when update category with non-existent id")
    public void update_withInvalidCategoryId_returnThrowException() {
        CreateCategoryRequestDto nonExistentRequestDto = new CreateCategoryRequestDto();
        nonExistentRequestDto.setName("non-existent category");
        nonExistentRequestDto.setDescription("non-existent description");

        lenient().when(categoryRepository.existsById(INVALID_ID)).thenReturn(false);
        assertThrows(RuntimeException.class,
                () -> categoryService.update(INVALID_ID, nonExistentRequestDto));
    }

    @Test
    @DisplayName("Verify delete method works")
    public void deleteById_withValidCategoryId_removeCategory() {
        Mockito.when(categoryRepository.existsById(categoryDrama.getId())).thenReturn(true);

        categoryService.deleteById(categoryDrama.getId());
        verify(categoryRepository).deleteById(categoryDrama.getId());
    }

    @Test
    @DisplayName("Verify exception was returned when delete category with non-existent id")
    public void deleteById_withInvalidCategoryId_returnThrowException() {
        Mockito.when(categoryRepository.existsById(INVALID_ID)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> categoryService.deleteById(INVALID_ID));
        verify(categoryRepository, never()).deleteById(INVALID_ID);
    }
}
