package ru.practicum.ewm.main.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.category.dto.CategoryDto;
import ru.practicum.ewm.main.category.dto.CategoryMapper;
import ru.practicum.ewm.main.category.model.Category;
import ru.practicum.ewm.main.category.repository.CategoryRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto addCategory(CategoryDto categoryDto) {
        Category addedCategory = categoryRepository.save(categoryMapper.toCategory(categoryDto));
        return categoryMapper.toCategoryDto(addedCategory);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        Category updatedCategory = findCategoryById(categoryDto.getId());
        updatedCategory.setName(categoryDto.getName());
        updatedCategory = categoryRepository.save(updatedCategory);
        return categoryMapper.toCategoryDto(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        findCategoryById(id);
        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        return categoryMapper.toCategoryDto(findCategoryById(id));
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageable)
                .stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("CategoryId=%d was not found", id)));
    }
}