package ru.practicum.explore.mapper;

import ru.practicum.explore.dto.category.CategoryDto;
import ru.practicum.explore.model.Category;

public class CategoryMapper {
    public static CategoryDto mapToDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category mapFromDto(CategoryDto categoryDto) {
        return Category.builder()
                .name(categoryDto.getName())
                .build();
    }
}
