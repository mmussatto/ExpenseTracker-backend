/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.services;

import dev.mmussatto.expensetracker.api.mappers.CategoryMapper;
import dev.mmussatto.expensetracker.api.model.CategoryDTO;
import dev.mmussatto.expensetracker.domain.Category;
import dev.mmussatto.expensetracker.domain.Color;
import dev.mmussatto.expensetracker.repositories.CategoryRepository;
import dev.mmussatto.expensetracker.services.exceptions.InvalidIdModificationException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
        assertEquals("/api/categories/1", categoryDTO.getPath());
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
        assertEquals("/api/categories/1", categoryDTO.getPath());
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

        assertEquals(savedCategory.getId(), savedDTO.getId());
        assertEquals(savedCategory.getName(), savedDTO.getName());
        assertEquals(savedCategory.getColor(), savedDTO.getColor());
        assertEquals("/api/categories/1", savedDTO.getPath());
    }

    @Test
    void createNewCategory_NameAlreadyExists() {

        Category category = new Category(NAME, COLOR);
        category.setId(1);

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName(NAME);
        categoryDTO.setColor(COLOR);

        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(category));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> categoryService.createNewCategory(categoryDTO));
    }

    @Test
    void createNewCategory_IdAlreadyExists() {

        Category category = new Category(NAME, COLOR);
        category.setId(1);

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(1);
        categoryDTO.setName(NAME);
        categoryDTO.setColor(COLOR);

        when(categoryRepository.findById(anyInt())).thenReturn(Optional.of(category));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> categoryService.createNewCategory(categoryDTO));
    }


    @Test
    void updateCategoryById() {

        //CategoryDTO passed to updateCategoryById
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("TestUpdate");
        categoryDTO.setColor(Color.GREEN);

        //Category previously saved in the database
        Category originalCategory = new Category(NAME, COLOR);
        originalCategory.setId(ID);

        //Category modified and saved
        Category updatedCategory = new Category(categoryDTO.getName(), categoryDTO.getColor());
        updatedCategory.setId(originalCategory.getId());


        when(categoryRepository.findById(anyInt())).thenReturn(Optional.of(originalCategory));
        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);


        //CategoryDTO returned after saving updateCategory
        CategoryDTO savedDTO = categoryService.updateCategoryById(originalCategory.getId(), categoryDTO);

        assertEquals(originalCategory.getId(), savedDTO.getId());      //same id as before
        assertEquals(categoryDTO.getName(), savedDTO.getName());    //updated name
        assertEquals(categoryDTO.getColor(), savedDTO.getColor());  //updated color
        assertEquals("/api/categories/" + originalCategory.getId(), savedDTO.getPath());


        //Verify that the updatedCategory was saved
        verify(categoryRepository, times(1)).save(updatedCategory);
    }

    @Test
    void updateCategoryById_NotFound() {

        when(categoryRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.updateCategoryById(ID, new CategoryDTO()));
    }

    @Test
    void updateCategoryById_InvalidIdModification() {

        //CategoryDTO passed to updateCategoryById
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(2);   //this id is invalid, will throw exception
        categoryDTO.setName("TestUpdate");
        categoryDTO.setColor(Color.GREEN);

        //Category already in the database
        Category savedCategory = new Category(NAME, COLOR);
        savedCategory.setId(ID);

        when(categoryRepository.findById(anyInt())).thenReturn(Optional.of(savedCategory));

        assertThrows(InvalidIdModificationException.class,
                () -> categoryService.updateCategoryById(ID, categoryDTO));
    }


    @Test
    void updateCategoryByName() {

        //CategoryDTO passed to updateCategoryById
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("TestUpdate");
        categoryDTO.setColor(Color.GREEN);

        //Category previously saved in the database
        Category originalCategory = new Category(NAME, COLOR);
        originalCategory.setId(ID);

        //Category modified and saved
        Category updatedCategory = new Category(categoryDTO.getName(), categoryDTO.getColor());
        updatedCategory.setId(originalCategory.getId());

        when(categoryRepository.findByName(originalCategory.getName())).thenReturn(Optional.of(originalCategory));
        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);

        //CategoryDTO returned after saving updateCategory
        CategoryDTO savedDTO = categoryService.updateCategoryByName(originalCategory.getName(), categoryDTO);

        assertEquals(originalCategory.getId(), savedDTO.getId());   //same id as before
        assertEquals(categoryDTO.getName(), savedDTO.getName());    //updated name
        assertEquals(categoryDTO.getColor(), savedDTO.getColor());  //updated color
        assertEquals("/api/categories/" + originalCategory.getId(), savedDTO.getPath());

        //Verify that the updatedCategory was saved
        verify(categoryRepository, times(1)).save(updatedCategory);

    }

    @Test
    void updateCategoryByName_NotFound() {

        when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.updateCategoryByName(NAME, new CategoryDTO()));
    }

    @Test
    void updateCategoryByName_InvalidIdModification() {

        //CategoryDTO passed to updateCategoryById
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(2);   //this id is invalid, will throw exception
        categoryDTO.setName("TestUpdate");
        categoryDTO.setColor(Color.GREEN);

        //Category already in the database
        Category savedCategory = new Category(NAME, COLOR);
        savedCategory.setId(ID);

        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(savedCategory));

        assertThrows(InvalidIdModificationException.class,
                () -> categoryService.updateCategoryByName(savedCategory.getName(), categoryDTO));
    }

    /*
        The "categoryDTO" is passed to patchCategoryById() with a different name and color.
        The "originalCategory" represents the previous unmodified state.
        The "updatedCategory" is returned when findById() is invoked inside patchCategoryById(). It has its name and
        color modified to match the categoryDTO, and then it is saved in the repository.
        The "savedDTO" is returned from patchCategoryById() with the updated name and color.

        The difference between this test and the update test is that the Optional returned by findById() is the
        one being modified inside the function. In the update, a new object is created. Because of this, here
        the updateCategory is the same as the Original and there the updatedCategory is the final state intended.
     */
    @Test
    void patchCategoryById() {

        //CategoryDTO passed to updateCategoryById
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("TestUpdate");
        categoryDTO.setColor(Color.GREEN);

        //Category previously saved in the database
        Category originalCategory = new Category(NAME, COLOR);
        originalCategory.setId(ID);

        //Category modified by function and saved
        Category updatedCategory = new Category(NAME, COLOR);
        updatedCategory.setId(ID);


        when(categoryRepository.findById(originalCategory.getId())).thenReturn(Optional.of(updatedCategory));
        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);


        //CategoryDTO returned after saving updateCategory
        CategoryDTO savedDTO = categoryService.patchCategoryById(originalCategory.getId(), categoryDTO);

        //Assert that the savedDTO is as expected
        assertEquals(originalCategory.getId(), savedDTO.getId());   //same id as before
        assertEquals(categoryDTO.getName(), savedDTO.getName());    //updated name
        assertEquals(categoryDTO.getColor(), savedDTO.getColor());  //updated color
        assertEquals("/api/categories/" + originalCategory.getId(), savedDTO.getPath());

        //Assert that the category was modified inside the function and was saved
        assertNotEquals(originalCategory.getName(), updatedCategory.getName());
        assertNotEquals(originalCategory.getColor(), updatedCategory.getColor());

        verify(categoryRepository, times(1)).save(updatedCategory);

    }

    @Test
    void patchCategoryById_changeOnlyName() {

        //CategoryDTO passed to updateCategoryById
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("TestUpdate");
//        categoryDTO.setColor(Color.GREEN);

        //Category previously saved in the database
        Category originalCategory = new Category(NAME, COLOR);
        originalCategory.setId(ID);

        //Category modified by function and saved
        Category updatedCategory = new Category(NAME, COLOR);
        updatedCategory.setId(ID);


        when(categoryRepository.findById(originalCategory.getId())).thenReturn(Optional.of(updatedCategory));
        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);


        //CategoryDTO returned after saving updateCategory
        CategoryDTO savedDTO = categoryService.patchCategoryById(originalCategory.getId(), categoryDTO);

        //Assert that the savedDTO is as expected
        assertEquals(originalCategory.getId(), savedDTO.getId());        //same id as before
        assertEquals(categoryDTO.getName(), savedDTO.getName());         //updated name
        assertEquals(originalCategory.getColor(), savedDTO.getColor());  //same color as before
        assertEquals("/api/categories/" + originalCategory.getId(), savedDTO.getPath());

        //Assert that only the name was modified inside the function and was saved
        assertNotEquals(originalCategory.getName(), updatedCategory.getName());
        assertEquals(originalCategory.getColor(), updatedCategory.getColor());

        verify(categoryRepository, times(1)).save(updatedCategory);
    }

    @Test
    void patchCategoryById_changeOnlyColor() {

        //CategoryDTO passed to updateCategoryById
        CategoryDTO categoryDTO = new CategoryDTO();
//        categoryDTO.setName("TestUpdate");
        categoryDTO.setColor(Color.GREEN);

        //Category previously saved in the database
        Category originalCategory = new Category(NAME, COLOR);
        originalCategory.setId(ID);

        //Category modified by function and saved
        Category updatedCategory = new Category(NAME, COLOR);
        updatedCategory.setId(ID);


        when(categoryRepository.findById(originalCategory.getId())).thenReturn(Optional.of(updatedCategory));
        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);


        //CategoryDTO returned after saving updateCategory
        CategoryDTO savedDTO = categoryService.patchCategoryById(originalCategory.getId(), categoryDTO);

        //Assert that the savedDTO is as expected
        assertEquals(originalCategory.getId(), savedDTO.getId());       //same id as before
        assertEquals(originalCategory.getName(), savedDTO.getName());   //same name as before
        assertEquals(categoryDTO.getColor(), savedDTO.getColor());      //updated color
        assertEquals("/api/categories/" + originalCategory.getId(), savedDTO.getPath());

        //Assert that the category was modified inside the function and was saved
        assertEquals(originalCategory.getName(), updatedCategory.getName());
        assertNotEquals(originalCategory.getColor(), updatedCategory.getColor());

        verify(categoryRepository, times(1)).save(updatedCategory);
    }

    @Test
    void patchCategoryById_NotFound() {

        when(categoryRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.patchCategoryById(ID, new CategoryDTO()));
    }

    @Test
    void patchCategoryById_InvalidIdModification() {

        //CategoryDTO passed to updateCategoryById
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(15); //attempt to change id
        categoryDTO.setName("TestUpdate");
        categoryDTO.setColor(Color.GREEN);

        //Category previously saved in the database
        Category originalCategory = new Category(NAME, COLOR);
        originalCategory.setId(ID); //original id


        when(categoryRepository.findById(originalCategory.getId())).thenReturn(Optional.of(originalCategory));


        assertThrows(InvalidIdModificationException.class,
                () -> categoryService.patchCategoryById(ID, categoryDTO));
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