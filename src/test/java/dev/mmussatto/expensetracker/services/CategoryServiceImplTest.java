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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    CategoryRepository categoryRepository;

    CategoryService categoryService;

    @Captor
    ArgumentCaptor<Category> categoryCaptor;

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

        Category savedEntity = createCategoryEntity();

        when(categoryRepository.findById(savedEntity.getId())).thenReturn(Optional.of(savedEntity));

        CategoryDTO returnDTO = categoryService.getCategoryById(ID);

        assertEquals(savedEntity.getId(), returnDTO.getId());
        assertEquals(savedEntity.getName(), returnDTO.getName());
        assertEquals(savedEntity.getColor(), returnDTO.getColor());
        assertEquals(savedEntity.getTransactions(), returnDTO.getTransactions());
        assertEquals("/api/categories/" + savedEntity.getId(), returnDTO.getPath());
    }

    @Test
    void getCategoryById_NotFound() {

        when(categoryRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(ID));
    }

    @Test
    void getCategoryByName() {

        Category savedEntity = createCategoryEntity();

        when(categoryRepository.findByName(savedEntity.getName())).thenReturn(Optional.of(savedEntity));

        CategoryDTO categoryDTO = categoryService.getCategoryByName(NAME);

        assertEquals(savedEntity.getId(), categoryDTO.getId());
        assertEquals(savedEntity.getName(), categoryDTO.getName());
        assertEquals(savedEntity.getColor(), categoryDTO.getColor());
        assertEquals(savedEntity.getTransactions(), categoryDTO.getTransactions());
        assertEquals("/api/categories/" + savedEntity.getId(), categoryDTO.getPath());
    }

    @Test
    void getCategoryByName_NotFound() {

        when(categoryRepository.findByName(NAME)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryByName(NAME));
    }

    @Test
    void createNewCategory() {

        //DTO passed to function
        CategoryDTO passedDTO = new CategoryDTO(NAME, COLOR);
        passedDTO.getTransactions().add(TRANSACTION);

        //Saved Entity
        Category savedEntity = new Category(passedDTO.getName(), passedDTO.getColor());
        savedEntity.setId(ID);
        savedEntity.setTransactions(passedDTO.getTransactions());


        when(categoryRepository.save(categoryCaptor.capture())).thenReturn(savedEntity);


        CategoryDTO savedDTO = categoryService.createNewCategory(passedDTO);

        assertEquals(savedEntity.getId(), savedDTO.getId());
        assertEquals(passedDTO.getName(), savedDTO.getName());
        assertEquals(passedDTO.getColor(), savedDTO.getColor());
        assertEquals(passedDTO.getTransactions(), savedDTO.getTransactions());
        assertEquals("/api/categories/" + savedEntity.getId(), savedDTO.getPath());

        Category capturedCategory = categoryCaptor.getValue();
        assertEquals(passedDTO.getName(), capturedCategory.getName());
        assertEquals(passedDTO.getColor(), capturedCategory.getColor());
        assertEquals(passedDTO.getTransactions(), capturedCategory.getTransactions());



//        argThat(capturedCategory.getName(), eq(savedEntity));
    }

    @Test
    void createNewCategory_NameAlreadyExists() {

        Category savedEntity = createCategoryEntity();

        CategoryDTO passedDTO = new CategoryDTO(NAME, COLOR);
        passedDTO.getTransactions().add(TRANSACTION);

        //When searching the repository by name, find an item
        when(categoryRepository.findByName(passedDTO.getName())).thenReturn(Optional.of(savedEntity));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> categoryService.createNewCategory(passedDTO));
    }

    @Test
    void createNewCategory_IdNotNull() {

        CategoryDTO passedDTO = new CategoryDTO(NAME, COLOR);
        passedDTO.getTransactions().add(TRANSACTION);

        assertThrows(RuntimeException.class,
                () -> categoryService.createNewCategory(passedDTO));
    }

    @Test
    void updateCategoryById() {

        //CategoryDTO passed to updateCategoryById
        CategoryDTO passedDTO = new CategoryDTO("TestUpdate", Color.GREEN);
        passedDTO.getTransactions().add(TRANSACTION);

        //Original Category
        Category originalCategory = createCategoryEntity();

        //Updated Category
        Category updatedCategory = new Category(passedDTO.getName(), passedDTO.getColor());
        updatedCategory.setId(originalCategory.getId());
        updatedCategory.setTransactions(passedDTO.getTransactions());


        when(categoryRepository.findById(originalCategory.getId())).thenReturn(Optional.of(originalCategory));
        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);


        //CategoryDTO returned after saving updateCategory
        CategoryDTO savedDTO = categoryService.updateCategoryById(originalCategory.getId(), passedDTO);

        assertEquals(originalCategory.getId(), savedDTO.getId());   //same id as before
        assertEquals(passedDTO.getName(), savedDTO.getName());      //updated name
        assertEquals(passedDTO.getColor(), savedDTO.getColor());    //updated color
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
        Category originalCategory = createCategoryEntity();

        //Return empty when searching repository
        when(categoryRepository.findById(originalCategory.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.updateCategoryById(originalCategory.getId(), passedDTO));
    }

    @Test
    void updateCategoryById_NameAlreadyExists() {

        //CategoryDTO passed to updateCategoryById
        CategoryDTO passedDTO = new CategoryDTO("TestUpdate", Color.GREEN);

        //Category already in the database
        Category original = createCategoryEntity();

        //Another saved category
        Category savedWithUpdateName = new Category("TestUpdate", Color.BLUE);
        savedWithUpdateName.setId(2);

        when(categoryRepository.findById(original.getId())).thenReturn(Optional.of(original));

        //When searching for name, return a category already saved with that name
        when(categoryRepository.findByName(passedDTO.getName())).thenReturn(Optional.of(savedWithUpdateName));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> categoryService.updateCategoryById(original.getId(), passedDTO));

        verify(categoryRepository, times(1)).findByName(passedDTO.getName());
    }

    @Test
    void patchCategoryById() {

        //CategoryDTO passed to updateCategoryById
        CategoryDTO passedDTO = new CategoryDTO("TestUpdate", Color.GREEN);
        Transaction passedTransaction = new Transaction();
        passedTransaction.setId(2);
        passedDTO.getTransactions().add(passedTransaction);

        //Category previously saved in the database
        Category originalCategory = createCategoryEntity();

        //Category modified by function and saved
        Category updatedCategory = new Category(originalCategory.getName(), originalCategory.getColor());
        updatedCategory.setId(originalCategory.getId());
        updatedCategory.setTransactions(originalCategory.getTransactions());


        //when searching repository, returned object with original values
        when(categoryRepository.findById(originalCategory.getId())).thenReturn(Optional.of(updatedCategory));

        //there is no other category already using the name
        when(categoryRepository.findByName(passedDTO.getName())).thenReturn(Optional.empty());

        //return the modified originalCategory after saving
        when(categoryRepository.save(categoryCaptor.capture())).thenReturn(updatedCategory);


        CategoryDTO savedDTO = categoryService.patchCategoryById(originalCategory.getId(), passedDTO);


        //Assert that the savedDTO is as expected
        assertEquals(originalCategory.getId(), savedDTO.getId());   //same id as before
        assertEquals(passedDTO.getName(), savedDTO.getName());      //updated name
        assertEquals(passedDTO.getColor(), savedDTO.getColor());    //updated color
        assertEquals(passedDTO.getTransactions(), savedDTO.getTransactions());    //updated transactions
        assertEquals("/api/categories/" + originalCategory.getId(), savedDTO.getPath());

        //Assert that the values were correctly updated before saving
        Category capturedCategory = categoryCaptor.getValue();
        assertEquals(originalCategory.getId(), capturedCategory.getId());
        assertEquals(passedDTO.getName(), capturedCategory.getName());
        assertEquals(passedDTO.getColor(), capturedCategory.getColor());
        assertEquals(passedDTO.getTransactions(), capturedCategory.getTransactions());
    }

    @Test
    void patchCategoryById_UpdateOnlyName() {

        //CategoryDTO passed to updateCategoryById
        CategoryDTO passedDTO = new CategoryDTO();
        passedDTO.setName("TestUpdate");

        //Category previously saved in the database
        Category originalCategory = createCategoryEntity();

        //Category modified by function and saved
        Category updatedCategory = new Category(originalCategory.getName(), originalCategory.getColor());
        updatedCategory.setId(originalCategory.getId());
        updatedCategory.setTransactions(originalCategory.getTransactions());


        //when searching repository, returned object with original values
        when(categoryRepository.findById(originalCategory.getId())).thenReturn(Optional.of(updatedCategory));

        //there is no other category already using the name
        when(categoryRepository.findByName(passedDTO.getName())).thenReturn(Optional.empty());

        //return the modified originalCategory after saving
        when(categoryRepository.save(categoryCaptor.capture())).thenReturn(updatedCategory);



        CategoryDTO savedDTO = categoryService.patchCategoryById(originalCategory.getId(), passedDTO);


        //Assert that the savedDTO is as expected
        assertEquals(originalCategory.getId(), savedDTO.getId());        //same id as before
        assertEquals(passedDTO.getName(), savedDTO.getName());           //updated name
        assertEquals(originalCategory.getColor(), savedDTO.getColor());  //same color as before
        assertEquals(originalCategory.getTransactions(), savedDTO.getTransactions()); //same transactions
        assertEquals("/api/categories/" + originalCategory.getId(), savedDTO.getPath());


        //Assert that the values were correctly updated before saving
        Category capturedCategory = categoryCaptor.getValue();
        assertEquals(originalCategory.getId(), capturedCategory.getId());
        assertEquals(passedDTO.getName(), capturedCategory.getName());
        assertEquals(originalCategory.getColor(), capturedCategory.getColor());
        assertEquals(originalCategory.getTransactions(), capturedCategory.getTransactions());
    }

    @Test
    void patchCategoryById_UpdateOnlyColor() {

        //CategoryDTO passed to updateCategoryById
        CategoryDTO passedDTO = new CategoryDTO();
        passedDTO.setColor(Color.GREEN);

        //Category previously saved in the database
        Category originalCategory = createCategoryEntity();


        //Category modified by function and saved
        Category updatedCategory = new Category(originalCategory.getName(), originalCategory.getColor());
        updatedCategory.setId(originalCategory.getId());
        updatedCategory.setTransactions(originalCategory.getTransactions());


        //when searching repository, returned object with original values
        when(categoryRepository.findById(originalCategory.getId())).thenReturn(Optional.of(updatedCategory));


        //return the modified originalCategory after saving
        when(categoryRepository.save(categoryCaptor.capture())).thenReturn(updatedCategory);


        //CategoryDTO returned after saving updateCategory
        CategoryDTO savedDTO = categoryService.patchCategoryById(originalCategory.getId(), passedDTO);

        //Assert that the savedDTO is as expected
        assertEquals(originalCategory.getId(), savedDTO.getId());       //same id as before
        assertEquals(originalCategory.getName(), savedDTO.getName());   //same name as before
        assertEquals(passedDTO.getColor(), savedDTO.getColor());      //updated color
        assertEquals(originalCategory.getTransactions(), savedDTO.getTransactions()); //same transactions
        assertEquals("/api/categories/" + originalCategory.getId(), savedDTO.getPath());

        //Assert that the values were correctly updated before saving
        Category capturedCategory = categoryCaptor.getValue();
        assertEquals(originalCategory.getId(), capturedCategory.getId());
        assertEquals(originalCategory.getName(), capturedCategory.getName());
        assertEquals(passedDTO.getColor(), capturedCategory.getColor());
        assertEquals(originalCategory.getTransactions(), capturedCategory.getTransactions());
    }

    @Test
    void patchCategoryById_UpdateOnlyTransactions() {

        //CategoryDTO passed to updateCategoryById
        CategoryDTO passedDTO = new CategoryDTO();
        Transaction passedTransaction = new Transaction();
        passedTransaction.setId(2);
        passedDTO.getTransactions().add(passedTransaction);

        //Category previously saved in the database
        Category originalCategory = createCategoryEntity();

        //Category returned after save
        Category updatedCategory = new Category(originalCategory.getName(), originalCategory.getColor());
        updatedCategory.setId(originalCategory.getId());
        updatedCategory.setTransactions(originalCategory.getTransactions());


        //when searching repository, returned object with original values
        when(categoryRepository.findById(originalCategory.getId())).thenReturn(Optional.of(updatedCategory));

        //return updatedCategory after saving
        when(categoryRepository.save(categoryCaptor.capture())).thenReturn(updatedCategory);


        CategoryDTO savedDTO = categoryService.patchCategoryById(originalCategory.getId(), passedDTO);


        //Assert that the savedDTO is as expected
        assertEquals(originalCategory.getId(), savedDTO.getId());       //same id as before
        assertEquals(originalCategory.getName(), savedDTO.getName());   //same name as before
        assertEquals(originalCategory.getColor(), savedDTO.getColor()); //same color as before
        assertEquals(passedDTO.getTransactions(), savedDTO.getTransactions()); //updated transactions
        assertEquals("/api/categories/" + originalCategory.getId(), savedDTO.getPath());


        //Assert that the values were correctly updated before saving
        Category capturedCategory = categoryCaptor.getValue();
        assertEquals(originalCategory.getName(), capturedCategory.getName());
        assertEquals(originalCategory.getColor(), capturedCategory.getColor());
        assertEquals(passedDTO.getTransactions(), capturedCategory.getTransactions());
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
                () -> categoryService.patchCategoryById(originalCategory.getId(), passedDTO));
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
    void patchCategoryById_NameAlreadyInUse() {

        //CategoryDTO passed to updateCategoryById
        CategoryDTO passedDTO = new CategoryDTO("TestUpdate", Color.GREEN);
        Transaction passedTransaction = new Transaction();
        passedTransaction.setId(2);
        passedDTO.getTransactions().add(passedTransaction);

        //Category previously saved in the database
        Category originalCategory = createCategoryEntity();

        //Another saved category
        Category savedWithUpdateName = new Category("TestUpdate", Color.BLUE);
        savedWithUpdateName.setId(2);

        //Category returned after save
        Category updatedCategory = new Category(originalCategory.getName(), originalCategory.getColor());
        updatedCategory.setId(originalCategory.getId());
        updatedCategory.setTransactions(originalCategory.getTransactions());


        when(categoryRepository.findById(originalCategory.getId())).thenReturn(Optional.of(updatedCategory));

        when(categoryRepository.findByName(passedDTO.getName())).thenReturn(Optional.of(savedWithUpdateName));


        assertThrows(ResourceAlreadyExistsException.class,
                () -> categoryService.patchCategoryById(originalCategory.getId(), passedDTO));
    }

    @Test
    void deleteCategoryById() {

        categoryService.deleteCategoryById(ID);

        verify(categoryRepository, times(1)).deleteById(anyInt());
    }

    @Test
    void getTransactionsById() {
        Transaction t1 = new Transaction();
        t1.setId(1);
        t1.setAmount(53.00);
        t1.setDescription("Test Transaction 1");

        Transaction t2 = new Transaction();
        t2.setId(2);
        t2.setAmount(123.00);
        t2.setDescription("Test Transaction 2");

        Set<Transaction> transactions = new HashSet<>(Arrays.asList(t1, t2));

        Category category = new Category(NAME, COLOR);
        category.setId(ID);
        t1.setCategory(category);
        t2.setCategory(category);
        category.setTransactions(transactions);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        Set<Transaction> returnTransactions = categoryService.getTransactionsById(category.getId());

        assertEquals(category.getTransactions().size(), returnTransactions.size());
        assertEquals(category.getTransactions(), returnTransactions);
    }

    @Test
    void getTransactionsById_NotFound() {
        Transaction t1 = new Transaction();
        t1.setId(1);
        t1.setAmount(53.00);
        t1.setDescription("Test Transaction 1");

        Transaction t2 = new Transaction();
        t2.setId(2);
        t2.setAmount(123.00);
        t2.setDescription("Test Transaction 2");

        Set<Transaction> transactions = new HashSet<>(Arrays.asList(t1, t2));

        Category category = new Category(NAME, COLOR);
        category.setId(ID);
        t1.setCategory(category);
        t2.setCategory(category);
        category.setTransactions(transactions);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getTransactionsById(category.getId()));
    }


    private static Category createCategoryEntity() {
        Category category = new Category(NAME, COLOR);
        category.setId(ID);
        category.getTransactions().add(TRANSACTION);
        return category;
    }
}