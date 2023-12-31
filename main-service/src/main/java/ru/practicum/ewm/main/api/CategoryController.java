package ru.practicum.ewm.main.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.category.dto.CategoryDto;
import ru.practicum.ewm.main.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    @ResponseStatus(OK)
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                           @RequestParam(defaultValue = "10") @Positive Integer size) {
        List<CategoryDto> categories = categoryService.getCategories(from, size);
        log.info("Categories list with size={} has been got", categories.size());
        return categories;
    }

    @GetMapping("/{catId}")
    @ResponseStatus(OK)
    public CategoryDto getCategoryInfoById(@PathVariable Long catId) {
        CategoryDto category = categoryService.getCategoryById(catId);
        log.info("Category {id={},name={}} has been got", category.getId(), category.getName());
        return category;
    }
}