package ru.practicum.ewm.main.category.dto;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.main.category.model.Category;

@Component
public class CategoryDtoMapper {
    public Category toCategory(CategoryDto categoryDto) {
        return Category.builder()
                .name(categoryDto.getName())
                .build();
    }

    public CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}