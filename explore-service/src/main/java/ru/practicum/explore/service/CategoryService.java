package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explore.dto.category.CategoryDto;
import ru.practicum.explore.exception.exceptions.CannotDeleteCategoryException;
import ru.practicum.explore.exception.exceptions.CategoryExistsException;
import ru.practicum.explore.exception.exceptions.CategoryNotFoundException;
import ru.practicum.explore.mapper.CategoryMapper;
import ru.practicum.explore.model.Category;
import ru.practicum.explore.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryDto save(CategoryDto categoryDto) {
        try {
            log.info("Adding category: {}.", categoryDto);
            Category category = categoryRepository.save(CategoryMapper.mapFromDto(categoryDto));
            log.info("Category saved: {}", category);
            return CategoryMapper.mapToDto(category);
        } catch (DataIntegrityViolationException e) {
            log.info("Failed to save category: {}", e.getMessage());
            throw new CategoryExistsException(e.getMessage());
        }
    }

    public CategoryDto patch(Long id, CategoryDto categoryDto) {
        try {
            log.info("Patching category id {} with {}.", id, categoryDto);
            Category categoryToPatch = getCategory(id);
            categoryToPatch.setName(categoryDto.getName());
            Category patchedCategory = categoryRepository.save(categoryToPatch);
            log.info("Category patched: {}", patchedCategory);
            return CategoryMapper.mapToDto(patchedCategory);
        } catch (DataIntegrityViolationException e) {
            log.info("Failed to update category: {}", e.getMessage());
            throw new CategoryExistsException(e.getMessage());
        }
    }

    public CategoryDto delete(Long id) {
        try {
            log.info("Deleting category id {}.", id);
            Category categoryToDelete = getCategory(id);
            categoryRepository.deleteById(id);
            log.info("Category deleted: {}.", categoryToDelete);
            return CategoryMapper.mapToDto(categoryToDelete);
        } catch (DataIntegrityViolationException e) {
            log.warn("Category id {} cannot be deleted: {}", id, e.getMessage());
            throw new CannotDeleteCategoryException(String
                    .format("Category id %s cannot be deleted: %s", id, e.getMessage()));
        }
    }

    public CategoryDto findCategory(Long id) {
        log.info("Looking for category id {}.", id);
        Category category = getCategory(id);
        log.info("Category found: {}", category);
        return CategoryMapper.mapToDto(category);
    }

    public List<CategoryDto> findAllCategories(Integer from, Integer size) {
        log.info("Getting all categories.");
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Category> categories = categoryRepository.findAllBy(page).getContent();
        log.info("Categories found: {}.", categories);
        return categories.stream().map(CategoryMapper::mapToDto).collect(Collectors.toList());
    }

    private Category getCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> {
            log.error("Category id {} not found.", id);
            return new CategoryNotFoundException(String.format("Category id %s not found.", id));
        });
    }
}
