package ru.practicum.explore.controller.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.dto.category.CategoryDto;
import ru.practicum.explore.service.CategoryService;
import ru.practicum.explore.service.CompilationService;
import ru.practicum.explore.service.EventService;

import javax.validation.constraints.Min;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/categories")
@Validated
public class CategoryPublicController {
    private final EventService eventService;
    private final CompilationService compilationService;
    private final CategoryService categoryService;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping()
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "10") Integer size) {
        log.info("Getting all categories.");
        List<CategoryDto> categories = categoryService.findAllCategories(from, size);
        log.info("Categories found: {}.", categories);
        return categories;
    }

    @GetMapping("/{catId}")
    public CategoryDto findCategory(@PathVariable @Min(1) Long catId) {
        log.info("Looking for category: {}.", catId);
        CategoryDto category = categoryService.findCategory(catId);
        log.info("Category found: {}.", category);
        return category;
    }
}
