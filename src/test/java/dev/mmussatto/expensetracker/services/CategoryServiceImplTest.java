/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.services;

import dev.mmussatto.expensetracker.api.mappers.CategoryMapper;
import dev.mmussatto.expensetracker.api.model.CategoryDTO;
import dev.mmussatto.expensetracker.domain.Category;
import dev.mmussatto.expensetracker.domain.Color;
import dev.mmussatto.expensetracker.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    CategoryRepository categoryRepository;

    CategoryService categoryService;

    public static final Integer ID = 1;
    public static final String NAME = "Test";
    public static final Color COLOR = Color.BLUE;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryServiceImpl(CategoryMapper.INSTANCE, categoryRepository);
    }

    @Test
    void getAllCategories() {
        List<Category> categories = Arrays.asList(new Category(), new Category(), new Category());

        when(categoryRepository.findAll()).thenReturn(categories);

        List<CategoryDTO> categoryDTOS = categoryService.getAllCategories();

        assertEquals(3, categoryDTOS.size());
    }

    @Test
    void getCategoryById() {
        Category category = new Category(NAME, COLOR);
        category.setId(ID);

        when(categoryRepository.findById(anyInt())).thenReturn(Optional.of(category));

        CategoryDTO categoryDTO = categoryService.getCategoryById(ID);

        assertEquals(ID, categoryDTO.getId());
        assertEquals(NAME, categoryDTO.getName());
        assertEquals(COLOR, categoryDTO.getColor());
        assertEquals("/api/categories/1", categoryDTO.getUrl());
    }

    @Test
    void getCategoryByName() {
        Category category = new Category(NAME, COLOR);
        category.setId(ID);

        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(category));

        CategoryDTO categoryDTO = categoryService.getCategoryByName(NAME);

        assertEquals(ID, categoryDTO.getId());
        assertEquals(NAME, categoryDTO.getName());
        assertEquals(COLOR, categoryDTO.getColor());
        assertEquals("/api/categories/1", categoryDTO.getUrl());
    }

    @Test
    void createNewCategory() {

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName(NAME);

        Category savedCategory = new Category(NAME, COLOR);
        savedCategory.setId(ID);

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        CategoryDTO savedDTO = categoryService.createNewCategory(categoryDTO);

        assertEquals(categoryDTO.getName(), savedDTO.getName());
        assertEquals("/api/categories/1", savedDTO.getUrl());
    }


    @Test
    void deleteCategoryById() {

        categoryService.deleteCategoryById(ID);

        verify(categoryRepository, times(1)).deleteById(anyInt());
    }

    @Test
    void deleteCategoryByName() {
        categoryService.deleteCategoryByName(NAME);

        verify(categoryRepository, times(1)).deleteByName(anyString());
    }
}