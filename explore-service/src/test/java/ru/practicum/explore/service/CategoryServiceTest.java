package ru.practicum.explore.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.explore.dto.category.CategoryDto;
import ru.practicum.explore.exception.exceptions.CategoryExistsException;
import ru.practicum.explore.exception.exceptions.CategoryNotFoundException;
import ru.practicum.explore.exception.exceptions.EmailOrNameRegisteredException;
import ru.practicum.explore.model.Category;
import ru.practicum.explore.repository.CategoryRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private CategoryDto categoryToSave;
    private Category savedCategory;
    private CategoryDto expectedCategory;

    @BeforeEach
    public void init() {
        categoryToSave = CategoryDto.builder()
                .name("concert")
                .build();

        savedCategory = Category.builder()
                .id(1L)
                .name(categoryToSave.getName())
                .build();

        expectedCategory = CategoryDto.builder()
                .id(savedCategory.getId())
                .name(savedCategory.getName())
                .build();
    }

    @Test
    public void savedCategory_Normal() {
        when(categoryRepository.save(any())).thenReturn(savedCategory);

        CategoryDto actualCategory = categoryService.save(categoryToSave);

        assertEquals(expectedCategory, actualCategory);
    }

    @Test
    public void saveCategory_NameExists() {
        when(categoryRepository.save(any())).thenThrow(new DataIntegrityViolationException(""));

        assertThrows(EmailOrNameRegisteredException.class, () -> categoryService.save(categoryToSave));
    }

    @Test
    public void patchCategory_Normal() {
        when(categoryRepository.findById(any())).thenReturn(Optional.of(savedCategory));
        Category updatedCategory = Category.builder().id(savedCategory.getId()).name("updated name").build();
        when(categoryRepository.save(any())).thenReturn(updatedCategory);

        CategoryDto actualCategory = categoryService.patch(1L, CategoryDto.builder().name("updated name").build());

        assertEquals(1L, actualCategory.getId());
        assertEquals("updated name", actualCategory.getName());
    }

    @Test
    public void patchCategory_WrongId() {
        when(categoryRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.patch(1L, categoryToSave));

        verify(categoryRepository, never()).save(any());
    }

    @Test
    public void patchCategory_NameExists() {
        when(categoryRepository.findById(any())).thenReturn(Optional.of(savedCategory));
        when(categoryRepository.save(any())).thenThrow(new DataIntegrityViolationException(""));

        assertThrows(CategoryExistsException.class, () -> categoryService.patch(1L, categoryToSave));
    }

    @Test
    public void deleteCategory_Normal() {
        when(categoryRepository.findById(any())).thenReturn(Optional.of(savedCategory));

        CategoryDto actualCategory = categoryService.delete(1L);

        assertEquals(expectedCategory, actualCategory);
    }

    @Test
    public void deleteCategory_NoSuchCategory() {
        when(categoryRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.delete(1L));

        verify(categoryRepository, never()).deleteById(any());
    }
}
