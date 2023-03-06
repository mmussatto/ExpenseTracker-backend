/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.services;

import dev.mmussatto.expensetracker.api.mappers.CategoryMapper;
import dev.mmussatto.expensetracker.api.model.CategoryDTO;
import dev.mmussatto.expensetracker.domain.Category;
import dev.mmussatto.expensetracker.domain.Color;
import dev.mmussatto.expensetracker.domain.Transaction;
import dev.mmussatto.expensetracker.repositories.CategoryRepository;
import dev.mmussatto.expensetracker.services.exceptions.InvalidIdModificationException;
import dev.mmussatto.expensetracker.services.exceptions.ResourceAlreadyExistsException;
import dev.mmussatto.expensetracker.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
    public static final Transaction TRANSACTION = new Transaction();

    @BeforeEach
    void setUp() {
        categoryService = new CategoryServiceImpl(CategoryMapper.INSTANCE, categoryRepository);
        TRANSACTION.setId(1);
    }

    @Test
    void getAllCategories() {

        Category c1 = new Category();
        c1.setId(1);

        Category c2 = new Category();
        c2.setId(2);
        List<Category> categories = Arrays.asList(c1, c2);

        when(categoryRepository.findAll()).thenReturn(categories);

        List<CategoryDTO> returnedList = categoryService.getAllCategories();

        assertEquals(2, returnedList.size());
        assertEquals("/api/categories/" + c1.getId(),returnedList.get(0).getPath());
        assertEquals("/api/categories/" + c2.getId(),returnedList.get(1).getPath());
    }

    @Test
    void getCategoryById() {
        Category category = new Category(NAME, COLOR);
        category.setId(ID);
        category.getTransactions().add(TRANSACTION);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        CategoryDTO categoryDTO = categoryService.getCategoryById(ID);

        assertEquals(category.getId(), categoryDTO.getId());
        assertEquals(category.getName(), categoryDTO.getName());
        assertEquals(category.getColor(), categoryDTO.getColor());
        assertEquals(category.getTransactions(), categoryDTO.getTransactions());
        assertEquals("/api/categories/" + category.getId(), categoryDTO.getPath());
    }

    @Test
    void getCategoryById_NotFound() {

        when(categoryRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(ID));
    }

    @Test
    void getCategoryByName() {

        Category category = new Category(NAME, COLOR);
        category.setId(ID);
        category.getTransactions().add(TRANSACTION);

        when(categoryRepository.findByName(category.getName())).thenReturn(Optional.of(category));

        CategoryDTO categoryDTO = categoryService.getCategoryByName(NAME);

        assertEquals(category.getId(), categoryDTO.getId());
        assertEquals(category.getName(), categoryDTO.getName());
        assertEquals(category.getColor(), categoryDTO.getColor());
        assertEquals(category.getTransactions(), categoryDTO.getTransactions());
        assertEquals("/api/categories/" + category.getId(), categoryDTO.getPath());
    }

    @Test
    void getCategoryByName_NotFound() {

        when(categoryRepository.findByName(NAME)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryByName(NAME));
    }

    @Test
    void createNewCategory() {

        //DTO passed to function
        CategoryDTO categoryDTO = new CategoryDTO(NAME, COLOR);
        categoryDTO.getTransactions().add(TRANSACTION);

        //Saved Entity
        Category category = new Category(categoryDTO.getName(), categoryDTO.getColor());
        category.setId(ID);
        category.setTransactions(categoryDTO.getTransactions());


        //Check if entity is saved correctly
        ArgumentMatcher<Category> argumentMatcher = categoryToSave ->
                Objects.equals(categoryToSave.getName(), category.getName())
                && Objects.equals(categoryToSave.getColor(), category.getColor())
                && Objects.equals(categoryToSave.getTransactions(), category.getTransactions());


        when(categoryRepository.save(argThat(argumentMatcher))).thenReturn(category);


        CategoryDTO savedDTO = categoryService.createNewCategory(categoryDTO);

        assertEquals(category.getId(), savedDTO.getId());
        assertEquals(category.getName(), savedDTO.getName());
        assertEquals(category.getColor(), savedDTO.getColor());
        assertEquals(category.getTransactions(), savedDTO.getTransactions());
        assertEquals("/api/categories/" + category.getId(), savedDTO.getPath());
    }

    @Test
    void createNewCategory_NameAlreadyExists() {

        Category category = new Category(NAME, COLOR);
        category.setId(ID);
        category.getTransactions().add(TRANSACTION);

        CategoryDTO categoryDTO = new CategoryDTO(NAME, COLOR);
        categoryDTO.getTransactions().add(TRANSACTION);

        //When searching the repository by name, find an item
        when(categoryRepository.findByName(categoryDTO.getName())).thenReturn(Optional.of(category));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> categoryService.createNewCategory(categoryDTO));
    }

    @Test
    void createNewCategory_IdAlreadyExists() {

        Category category = new Category(NAME, COLOR);
        category.setId(ID);
        category.getTransactions().add(TRANSACTION);

        CategoryDTO categoryDTO = new CategoryDTO(NAME, COLOR);
        categoryDTO.setId(ID);
        categoryDTO.getTransactions().add(TRANSACTION);

        //When searching the repository by id, find an item
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> categoryService.createNewCategory(categoryDTO));
    }

    @Test
    void updateCategoryById() {

        //CategoryDTO passed to updateCategoryById
        CategoryDTO passedDTO = new CategoryDTO("TestUpdate", Color.GREEN);
        passedDTO.getTransactions().add(TRANSACTION);

        //Original Category
        Category originalCategory = new Category(NAME, COLOR);
        originalCategory.setId(ID);

        //Updated Category
        Category updatedCategory = new Category(passedDTO.getName(), passedDTO.getColor());
        updatedCategory.setId(originalCategory.getId());
        updatedCategory.setTransactions(passedDTO.getTransactions());


        when(categoryRepository.findById(originalCategory.getId())).thenReturn(Optional.of(originalCategory));
        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);


        //CategoryDTO returned after saving updateCategory
        CategoryDTO savedDTO = categoryService.updateCategoryById(originalCategory.getId(), passedDTO);

        assertEquals(originalCategory.getId(), savedDTO.getId());  //same id as before
        assertEquals(passedDTO.getName(), savedDTO.getName());    //updated name
        assertEquals(passedDTO.getColor(), savedDTO.getColor());  //updated color
        assertEquals(passedDTO.getTransactions(), savedDTO.getTransactions());  //updated transactions
        assertEquals("/api/categories/" + originalCategory.getId(), savedDTO.getPath());


        //Verify that the updatedCategory was saved
        verify(categoryRepository, times(1)).save(updatedCategory);
    }

    @Test
    void updateCategoryById_NotFound() {

        //CategoryDTO passed to updateCategoryById
        CategoryDTO passedDTO = new CategoryDTO("TestUpdate", Color.GREEN);
        passedDTO.getTransactions().add(TRANSACTION);

        //Original Category
        Category originalCategory = new Category(NAME, COLOR);
        originalCategory.setId(ID);

        //Return empty when searching repository
        when(categoryRepository.findById(originalCategory.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.updateCategoryById(originalCategory.getId(), passedDTO));
    }

    @Test
    void updateCategoryById_InvalidIdModification() {

        //CategoryDTO passed to updateCategoryById
        CategoryDTO passedDTO = new CategoryDTO("TestUpdate", Color.GREEN);
        passedDTO.setId(15);   //attempting to change id in update

        //Category already in the database
        Category original = new Category(NAME, COLOR);
        original.setId(ID);

        when(categoryRepository.findById(original.getId())).thenReturn(Optional.of(original));

        assertThrows(InvalidIdModificationException.class,
                () -> categoryService.updateCategoryById(original.getId(), passedDTO));
    }

    @Test
    void updateCategoryByName() {

        //CategoryDTO passed to updateCategoryByName
        CategoryDTO passedDTO = new CategoryDTO("TestUpdate", Color.GREEN);
        passedDTO.getTransactions().add(TRANSACTION);

        //Category previously saved in the database
        Category originalCategory = new Category(NAME, COLOR);
        originalCategory.setId(ID);

        //Category modified and saved
        Category updatedCategory = new Category(passedDTO.getName(), passedDTO.getColor());
        updatedCategory.setId(originalCategory.getId());
        updatedCategory.setTransactions(passedDTO.getTransactions());

        when(categoryRepository.findByName(originalCategory.getName())).thenReturn(Optional.of(originalCategory));
        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);

        //CategoryDTO returned after saving updateCategory
        CategoryDTO savedDTO = categoryService.updateCategoryByName(originalCategory.getName(), passedDTO);

        assertEquals(originalCategory.getId(), savedDTO.getId());   //same id as before
        assertEquals(passedDTO.getName(), savedDTO.getName());      //updated name
        assertEquals(passedDTO.getColor(), savedDTO.getColor());    //updated color
        assertEquals(passedDTO.getTransactions(), savedDTO.getTransactions());  //updated transactions
        assertEquals("/api/categories/" + originalCategory.getId(), savedDTO.getPath());

        //Verify that the updatedCategory was saved
        verify(categoryRepository, times(1)).save(updatedCategory);

    }

    @Test
    void updateCategoryByName_NotFound() {

        //CategoryDTO passed to updateCategoryByName
        CategoryDTO passedDTO = new CategoryDTO("TestUpdate", Color.GREEN);
        passedDTO.getTransactions().add(TRANSACTION);

        //Category previously saved in the database
        Category originalCategory = new Category(NAME, COLOR);
        originalCategory.setId(ID);

        when(categoryRepository.findByName(originalCategory.getName())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.updateCategoryByName(originalCategory.getName(), passedDTO));
    }

    @Test
    void updateCategoryByName_InvalidIdModification() {

        //CategoryDTO passed to updateCategoryByName
        CategoryDTO passedDTO = new CategoryDTO("TestUpdate", Color.GREEN);
        passedDTO.getTransactions().add(TRANSACTION);
        passedDTO.setId(15);   //attempting to change id in update


        //Category already in the database
        Category originalCategory = new Category(NAME, COLOR);
        originalCategory.setId(ID);

        when(categoryRepository.findByName(originalCategory.getName())).thenReturn(Optional.of(originalCategory));

        assertThrows(InvalidIdModificationException.class,
                () -> categoryService.updateCategoryByName(originalCategory.getName(), passedDTO));
    }

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
    void patchCategoryById_UpdateOnlyName() {

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
    void patchCategoryById_UpdateOnlyColor() {

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

        //CategoryDTO passed to updateCategoryById
        CategoryDTO passedDTO = new CategoryDTO("TestUpdate", Color.GREEN);
        passedDTO.getTransactions().add(TRANSACTION);

        //Category previously saved in the database
        Category originalCategory = new Category(NAME, COLOR);
        originalCategory.setId(ID);

        when(categoryRepository.findById(originalCategory.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.patchCategoryById(ID, new CategoryDTO()));
    }

    @Test
    void patchCategoryById_InvalidIdModification() {

        //CategoryDTO passed to updateCategoryById
        CategoryDTO passedDTO = new CategoryDTO("TestUpdate", Color.GREEN);
        passedDTO.getTransactions().add(TRANSACTION);
        passedDTO.setId(15); //attempting to change id in update


        //Category previously saved in the database
        Category originalCategory = new Category(NAME, COLOR);
        originalCategory.setId(ID); //original id


        when(categoryRepository.findById(originalCategory.getId())).thenReturn(Optional.of(originalCategory));


        assertThrows(InvalidIdModificationException.class,
                () -> categoryService.patchCategoryById(originalCategory.getId(), passedDTO));
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