/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.services;

import dev.mmussatto.expensetracker.api.mappers.CategoryMapper;
import dev.mmussatto.expensetracker.api.model.CategoryDTO;
import dev.mmussatto.expensetracker.domain.Category;
import dev.mmussatto.expensetracker.domain.Color;
import dev.mmussatto.expensetracker.repositories.CategoryRepository;
import dev.mmussatto.expensetracker.services.exceptions.ResourceAlreadyExistsException;
import dev.mmussatto.expensetracker.services.exceptions.ResourceNotFoundException;
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
    void getCategoryById_NotFound() {

        when(categoryRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(ID));
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
    void getCategoryByName_NotFound() {

        when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryByName(NAME));
    }

    @Test
    void createNewCategory() {

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName(NAME);

        Category savedCategory = new Category(NAME, COLOR);
        savedCategory.setId(ID);

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        CategoryDTO savedDTO = categoryService.createNewCategory(categoryDTO);

        assertEquals(ID, savedDTO.getId());
        assertEquals(categoryDTO.getName(), savedDTO.getName());
        assertEquals(COLOR, savedDTO.getColor());
        assertEquals("/api/categories/1", savedDTO.getUrl());
    }

    @Test
    void createNewCategory_AlreadyExists() {

        Category category = new Category(NAME, COLOR);
        category.setId(1);

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName(NAME);
        categoryDTO.setColor(COLOR);

        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(category));

        assertThrows(ResourceAlreadyExistsException.class, () -> categoryService.createNewCategory(categoryDTO));
    }


    @Test
    void updateCategoryById() {

        //CategoryDTO passed to updateCategoryById
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(2);
        categoryDTO.setColor(Color.GREEN);
        categoryDTO.setName(NAME);

        //Category already in the database
        Category savedCategory = new Category(NAME, COLOR);
        savedCategory.setId(ID);

        //Updated Category that will be saved in the database
        Category updatedCategory = new Category(categoryDTO.getName(), categoryDTO.getColor());
        updatedCategory.setId(savedCategory.getId());


        when(categoryRepository.findById(anyInt())).thenReturn(Optional.of(savedCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);


        //CategoryDTO returned after saving updateCategory
        CategoryDTO savedDTO = categoryService.updateCategoryById(savedCategory.getId(), categoryDTO);

        assertEquals(updatedCategory.getId(), savedDTO.getId());
        assertEquals(categoryDTO.getName(), savedDTO.getName());
        assertEquals(categoryDTO.getColor(), savedDTO.getColor());
        assertEquals("/api/categories/" + updatedCategory.getId(), savedDTO.getUrl());
    }

    @Test
    void updateCategoryById_NotFound() {

        when(categoryRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategoryById(ID, new CategoryDTO()));
    }


    @Test
    void updateCategoryByName() {

        //CategoryDTO passed to updateCategoryByName
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(2);
        categoryDTO.setName("TestUpdate");
        categoryDTO.setColor(Color.GREEN);

        //Category already in the database
        Category savedCategory = new Category(NAME, COLOR);
        savedCategory.setId(ID);

        //Updated Category that will be saved in the database
        Category updatedCategory = new Category(categoryDTO.getName(), categoryDTO.getColor());
        updatedCategory.setId(savedCategory.getId());

        when(categoryRepository.findByName(savedCategory.getName())).thenReturn(Optional.of(savedCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        //CategoryDTO returned after saving updateCategory
        CategoryDTO savedDTO = categoryService.updateCategoryByName(savedCategory.getName(), categoryDTO);

        assertEquals(updatedCategory.getId(), savedDTO.getId());
        assertEquals(categoryDTO.getName(), savedDTO.getName());
        assertEquals(categoryDTO.getColor(), savedDTO.getColor());
        assertEquals("/api/categories/" + updatedCategory.getId(), savedDTO.getUrl());

    }

    @Test
    void updateCategoryByName_NotFound() {

        when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategoryByName(NAME, new CategoryDTO()));
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

    @Test
    void patchCategoryById() {
    }
}