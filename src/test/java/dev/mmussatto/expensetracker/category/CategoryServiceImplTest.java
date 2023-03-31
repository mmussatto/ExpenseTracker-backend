/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.category;

import dev.mmussatto.expensetracker.entities.category.Category;
import dev.mmussatto.expensetracker.entities.category.CategoryRepository;
import dev.mmussatto.expensetracker.entities.category.CategoryServiceImpl;
import dev.mmussatto.expensetracker.entities.helpers.Color;
import dev.mmussatto.expensetracker.entities.transaction.Transaction;
import dev.mmussatto.expensetracker.exceptions.ResourceAlreadyExistsException;
import dev.mmussatto.expensetracker.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategoryServiceImpl categoryService;


    public static final Integer ID = 1;
    public static final String NAME = "Test";
    public static final Color COLOR = Color.BLUE;
    public static final Transaction TRANSACTION = new Transaction();
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 1;

    @BeforeAll
    static void initializeTransaction() {
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

        List<Category> returnedList = categoryService.getAllCategories();

        assertEquals(2, returnedList.size());
    }

    @Test
    void getCategoryById() {

        Category savedEntity = createCategoryEntity();

        when(categoryRepository.findById(savedEntity.getId())).thenReturn(Optional.of(savedEntity));

        Category returnedEntity = categoryService.getCategoryById(savedEntity.getId());

        assertEquals(savedEntity.getId(), returnedEntity.getId());
        assertEquals(savedEntity.getName(), returnedEntity.getName());
        assertEquals(savedEntity.getColor(), returnedEntity.getColor());
        assertEquals(savedEntity.getTransactions(), returnedEntity.getTransactions());
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

        Category returnedEntity = categoryService.getCategoryByName(savedEntity.getName());

        assertEquals(savedEntity.getId(), returnedEntity.getId());
        assertEquals(savedEntity.getName(), returnedEntity.getName());
        assertEquals(savedEntity.getColor(), returnedEntity.getColor());
        assertEquals(savedEntity.getTransactions(), returnedEntity.getTransactions());
    }

    @Test
    void getCategoryByName_NotFound() {

        when(categoryRepository.findByName(NAME)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryByName(NAME));
    }

    @Test
    void createNewCategory() {

        //Entity passed to function
        Category passedEntity = new Category(NAME, COLOR);
        passedEntity.getTransactions().add(TRANSACTION);

        //Saved Entity
        Category savedEntity = new Category(passedEntity.getName(), passedEntity.getColor());
        savedEntity.setId(ID);
        savedEntity.setTransactions(passedEntity.getTransactions());

        when(categoryRepository.findByName(passedEntity.getName())).thenReturn(Optional.empty());
        when(categoryRepository.save(passedEntity)).thenReturn(savedEntity);

        Category returnedEntity = categoryService.createNewCategory(passedEntity);

        assertEquals(savedEntity.getId(), returnedEntity.getId());
        assertEquals(passedEntity.getName(), returnedEntity.getName());
        assertEquals(passedEntity.getColor(), returnedEntity.getColor());
        assertEquals(passedEntity.getTransactions(), returnedEntity.getTransactions());
    }

    @Test
    void createNewCategory_NameAlreadyExists() {

        Category savedEntity = createCategoryEntity();

        Category passedEntity = new Category(NAME, COLOR);
        passedEntity.getTransactions().add(TRANSACTION);

        //When searching the repository by name, find an item
        when(categoryRepository.findByName(passedEntity.getName())).thenReturn(Optional.of(savedEntity));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> categoryService.createNewCategory(passedEntity));
    }

    @Test
    void updateCategoryById() {

        //Category passed to updateCategoryById
        Category passedEntity = new Category("TestUpdate", Color.GREEN);
        passedEntity.getTransactions().add(TRANSACTION);

        //Original Category
        Category originalCategory = createCategoryEntity();

        //Updated Category
        Category updatedCategory = new Category(passedEntity.getName(), passedEntity.getColor());
        updatedCategory.setId(originalCategory.getId());
        updatedCategory.setTransactions(passedEntity.getTransactions());


        when(categoryRepository.findById(originalCategory.getId())).thenReturn(Optional.of(originalCategory));
        when(categoryRepository.findByName(passedEntity.getName())).thenReturn(Optional.empty());
        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);


        //Category returned after saving updateCategory
        Category returnedEntity = categoryService.updateCategoryById(originalCategory.getId(), passedEntity);

        assertEquals(originalCategory.getId(), returnedEntity.getId());   //same id as before
        assertEquals(passedEntity.getName(), returnedEntity.getName());      //updated name
        assertEquals(passedEntity.getColor(), returnedEntity.getColor());    //updated color
        assertEquals(passedEntity.getTransactions(), returnedEntity.getTransactions());  //updated transactions


        //Verify that the updatedCategory was saved
        verify(categoryRepository, times(1)).findById(anyInt());
        verify(categoryRepository, times(1)).findByName(passedEntity.getName());
        verify(categoryRepository, times(1)).save(updatedCategory);
    }

    @Test
    void updateCategoryById_NotFound() {

        //Category passed to updateCategoryById
        Category passedEntity = new Category("TestUpdate", Color.GREEN);
        passedEntity.getTransactions().add(TRANSACTION);

        //Original Category
        Category originalCategory = createCategoryEntity();

        //Return empty when searching repository
        when(categoryRepository.findById(originalCategory.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.updateCategoryById(originalCategory.getId(), passedEntity));
    }

    @Test
    void updateCategoryById_NameAlreadyExists() {

        //Category passed to updateCategoryById
        Category passedEntity = new Category("TestUpdate", Color.GREEN);

        //Category already in the database
        Category original = createCategoryEntity();

        //Another saved category
        Category savedWithUpdateName = new Category(passedEntity.getName(), Color.BLUE);
        savedWithUpdateName.setId(2);

        when(categoryRepository.findById(original.getId())).thenReturn(Optional.of(original));

        //When searching by name, return a category already saved with that name
        when(categoryRepository.findByName(passedEntity.getName())).thenReturn(Optional.of(savedWithUpdateName));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> categoryService.updateCategoryById(original.getId(), passedEntity));

        verify(categoryRepository, times(1)).findByName(passedEntity.getName());
    }

    @Test
    void patchCategoryById() {

        //Category passed to updateCategoryById
        Category passedEntity = new Category("TestUpdate", Color.GREEN);
        Transaction passedTransaction = new Transaction();
        passedTransaction.setId(2);
        passedEntity.getTransactions().add(passedTransaction);

        //Category previously saved in the database
        Category originalCategory = createCategoryEntity();

        //Category modified by function and saved
        Category updatedCategory = new Category(passedEntity.getName(), passedEntity.getColor());
        updatedCategory.setId(originalCategory.getId());
        updatedCategory.setTransactions(passedEntity.getTransactions());


        //when searching repository, returned object with original values
        when(categoryRepository.findById(originalCategory.getId())).thenReturn(Optional.of(originalCategory));

        //there is no other category already using the name
        when(categoryRepository.findByName(passedEntity.getName())).thenReturn(Optional.empty());

        //return the modified originalCategory after saving
        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);


        Category returnedEntity = categoryService.patchCategoryById(originalCategory.getId(), passedEntity);


        //Assert that the returnedEntity is as expected
        assertEquals(originalCategory.getId(), returnedEntity.getId());   //same id as before
        assertEquals(passedEntity.getName(), returnedEntity.getName());      //updated name
        assertEquals(passedEntity.getColor(), returnedEntity.getColor());    //updated color
        assertEquals(passedEntity.getTransactions(), returnedEntity.getTransactions()); //updated transactions

        verify(categoryRepository, times(1)).save(updatedCategory);
    }

    @Test
    void patchCategoryById_UpdateOnlyName() {

        //Category passed to updateCategoryById
        Category passedEntity = new Category();
        passedEntity.setName("TestUpdate");

        //Category previously saved in the database
        Category originalCategory = createCategoryEntity();

        //Category modified by function and saved
        Category updatedCategory = new Category(originalCategory.getName(), originalCategory.getColor());
        updatedCategory.setId(originalCategory.getId());
        updatedCategory.setTransactions(originalCategory.getTransactions());


        //when searching repository, returned object with original values
        when(categoryRepository.findById(originalCategory.getId())).thenReturn(Optional.of(updatedCategory));

        //there is no other category already using the name
        when(categoryRepository.findByName(passedEntity.getName())).thenReturn(Optional.empty());

        //return the modified originalCategory after saving
        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);


        Category returnedEntity = categoryService.patchCategoryById(originalCategory.getId(), passedEntity);


        //Assert that the returnedEntity is as expected
        assertEquals(originalCategory.getId(), returnedEntity.getId());        //same id as before
        assertEquals(passedEntity.getName(), returnedEntity.getName());        //updated name
        assertEquals(originalCategory.getColor(), returnedEntity.getColor());  //same color as before
        assertEquals(originalCategory.getTransactions(), returnedEntity.getTransactions()); //same transactions

    }

    @Test
    void patchCategoryById_UpdateOnlyColor() {

        //Category passed to updateCategoryById
        Category passedEntity = new Category();
        passedEntity.setColor(Color.GREEN);

        //Category previously saved in the database
        Category originalCategory = createCategoryEntity();


        //Category modified by function and saved
        Category updatedCategory = createCategoryEntity();


        //when searching repository, returned object with original values
        when(categoryRepository.findById(originalCategory.getId())).thenReturn(Optional.of(updatedCategory));


        //return the modified originalCategory after saving
        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);


        //Category returned after saving updateCategory
        Category returnedEntity = categoryService.patchCategoryById(originalCategory.getId(), passedEntity);

        //Assert that the returnedEntity is as expected
        assertEquals(originalCategory.getId(), returnedEntity.getId());       //same id as before
        assertEquals(originalCategory.getName(), returnedEntity.getName());   //same name as before
        assertEquals(passedEntity.getColor(), returnedEntity.getColor());      //updated color
        assertEquals(originalCategory.getTransactions(), returnedEntity.getTransactions()); //same transactions

    }

    @Test
    void patchCategoryById_UpdateOnlyTransactions() {

        //Category passed to updateCategoryById
        Category passedEntity = new Category();
        Transaction passedTransaction = new Transaction();
        passedTransaction.setId(2);
        passedEntity.getTransactions().add(passedTransaction);

        //Category previously saved in the database
        Category originalCategory = createCategoryEntity();

        //Category returned after save
        Category updatedCategory = createCategoryEntity();


        //when searching repository, returned object with original values
        when(categoryRepository.findById(originalCategory.getId())).thenReturn(Optional.of(updatedCategory));

        //return updatedCategory after saving
        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);


        Category returnedEntity = categoryService.patchCategoryById(originalCategory.getId(), passedEntity);


        //Assert that the returnedEntity is as expected
        assertEquals(originalCategory.getId(), returnedEntity.getId());       //same id as before
        assertEquals(originalCategory.getName(), returnedEntity.getName());   //same name as before
        assertEquals(originalCategory.getColor(), returnedEntity.getColor()); //same color as before
        assertEquals(passedEntity.getTransactions(), returnedEntity.getTransactions()); //updated transactions

    }

    @Test
    void patchCategoryById_NotFound() {

        //Category passed to updateCategoryById
        Category passedEntity = new Category("TestUpdate", Color.GREEN);
        passedEntity.getTransactions().add(TRANSACTION);

        //Category previously saved in the database
        Category originalCategory = new Category(NAME, COLOR);
        originalCategory.setId(ID);

        when(categoryRepository.findById(originalCategory.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.patchCategoryById(originalCategory.getId(), passedEntity));
    }

    @Test
    void patchCategoryById_NameAlreadyInUse() {

        //Category passed to updateCategoryById
        Category passedEntity = new Category("TestUpdate", Color.GREEN);
        Transaction passedTransaction = new Transaction();
        passedTransaction.setId(2);
        passedEntity.getTransactions().add(passedTransaction);

        //Category previously saved in the database
        Category originalCategory = createCategoryEntity();

        //Another saved category
        Category savedWithUpdateName = new Category("TestUpdate", Color.BLUE);
        savedWithUpdateName.setId(2);

        //Category returned after save
        Category updatedCategory = createCategoryEntity();


        when(categoryRepository.findById(originalCategory.getId())).thenReturn(Optional.of(updatedCategory));

        when(categoryRepository.findByName(passedEntity.getName())).thenReturn(Optional.of(savedWithUpdateName));


        assertThrows(ResourceAlreadyExistsException.class,
                () -> categoryService.patchCategoryById(originalCategory.getId(), passedEntity));
    }

    @Test
    void deleteCategoryById() {

        when(categoryRepository.findById(ID)).thenReturn(Optional.of(new Category()));
        doNothing().when(categoryRepository).deleteById(ID);

        categoryService.deleteCategoryById(ID);

        verify(categoryRepository, times(1)).deleteById(anyInt());
    }

    @Test
    void deleteCategoryById_NotFound() {

        when(categoryRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategoryById(ID));
    }

    @Test
    void getTransactionsById() {

        //Create transactions
        Transaction t1 = new Transaction();
        t1.setId(1);
        t1.setAmount(53.00);
        t1.setDescription("Test Transaction 1");

        Transaction t2 = new Transaction();
        t2.setId(2);
        t2.setAmount(123.00);
        t2.setDescription("Test Transaction 2");

        List<Transaction> transactions = Arrays.asList(t1, t2);

        //Create category returned by the repository
        Category category = new Category(NAME, COLOR);
        category.setId(ID);
        t1.setCategory(category);
        t2.setCategory(category);
        category.setTransactions(transactions);

        //Create page returned by the service
        Pageable pageable = PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE, Sort.by("date"));
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), transactions.size());

        Page<Transaction> pagedTransactions = new PageImpl<Transaction>(
                transactions.subList(start, end), pageable, transactions.size());

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        Page<Transaction> returnPagedTransactions = categoryService.getTransactionsById(category.getId(), DEFAULT_PAGE, DEFAULT_SIZE);

        assertEquals(DEFAULT_SIZE, returnPagedTransactions.getContent().size(), "Wrong number of transactions");
        assertEquals(pagedTransactions, returnPagedTransactions);
    }

    @Test
    void getTransactionsById_NotFound() {

        Integer notFoundId = 123;

        when(categoryRepository.findById(notFoundId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                categoryService.getTransactionsById(notFoundId, DEFAULT_PAGE, DEFAULT_SIZE));
    }


    private static Category createCategoryEntity() {
        Category category = new Category(NAME, COLOR);
        category.setId(ID);
        category.getTransactions().add(TRANSACTION);
        return category;
    }
}