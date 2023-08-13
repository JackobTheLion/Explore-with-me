package ru.practicum.explore.controller.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.dto.category.CategoryDto;
import ru.practicum.explore.service.CategoryService;

import javax.validation.constraints.Min;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
@Validated
public class CategoryAdminController {

    private final CategoryService categoryService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @Validated CategoryDto categoryDto) {
        log.info("Adding category: {}.", categoryDto);
        CategoryDto savedCategory = categoryService.save(categoryDto);
        log.info("Category saved: {}", savedCategory);
        return savedCategory;
    }

    @PatchMapping("/{catId}")
    public CategoryDto patchCategory(@RequestBody @Validated CategoryDto categoryDto,
                                     @PathVariable @Min(1) Long catId) {
        log.info("Patching category id {} with {}.", catId, categoryDto);
        CategoryDto patchedCategory = categoryService.patch(catId, categoryDto);
        log.info("Category patched: {}", patchedCategory);
        return patchedCategory;
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable @Min(1) Long catId) {
        log.info("Deleting category id {}.", catId);
        categoryService.delete(catId);
    }
}