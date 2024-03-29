/*
 * Created by murilo.mussatto on 20/03/2023
 */


package dev.mmussatto.expensetracker.entities.transaction;

import dev.mmussatto.expensetracker.entities.category.Category;
import dev.mmussatto.expensetracker.entities.category.CategoryService;
import dev.mmussatto.expensetracker.entities.helpers.Color;
import dev.mmussatto.expensetracker.entities.paymentmethod.PaymentMethod;
import dev.mmussatto.expensetracker.entities.paymentmethod.PaymentMethodService;
import dev.mmussatto.expensetracker.entities.paymentmethod.PaymentType;
import dev.mmussatto.expensetracker.entities.tag.Tag;
import dev.mmussatto.expensetracker.entities.tag.TagService;
import dev.mmussatto.expensetracker.entities.vendor.Vendor;
import dev.mmussatto.expensetracker.entities.vendor.VendorService;
import dev.mmussatto.expensetracker.entities.vendor.onlinestore.OnlineStore;
import dev.mmussatto.expensetracker.entities.vendor.physicalstore.PhysicalStore;
import dev.mmussatto.expensetracker.exceptions.InvalidMonthException;
import dev.mmussatto.expensetracker.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    // -------------- Constants ----------------------------
    private static final Double AMOUNT = 115.00;
    private static final LocalDateTime DATE = LocalDateTime.now();
    private static final String DESCRIPTION = "Description";
    private static final Category CATEGORY = new Category("Test Category", Color.BLUE);
    private static final PaymentMethod PAYMENT_METHOD = new PaymentMethod("Test Payment Method", PaymentType.CASH);
    private static final Vendor VENDOR_OS = new OnlineStore("Test Online Store", "www.test.com");
    private static final Vendor VENDOR_PS = new PhysicalStore("Test Physical Store", "Test St.");
    private static final Tag TAG1 = new Tag("Test Tag 1", Color.BLUE);
    private static final Tag TAG2 = new Tag("Test Tag 2", Color.RED);
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 1;

    @Mock TransactionRepository transactionRepository;

    //Mock services
    @Mock CategoryService categoryService;
    @Mock PaymentMethodService paymentMethodService;
    @Mock VendorService vendorService;
    @Mock TagService tagService;

    @InjectMocks
    TransactionServiceImpl transactionService;



    @BeforeAll
    static void setUpEntities() {
        CATEGORY.setId(1);
        PAYMENT_METHOD.setId(1);
        VENDOR_OS.setId(1);
        VENDOR_PS.setId(2);
        TAG1.setId(1);
        TAG2.setId(2);
    }


    // -------------- READ ----------------------------
    @Test
    void getAllTransactions() {
        List<Transaction> savedList = createTransactionList();

        when(transactionRepository.findAll()).thenReturn(savedList);

        List<Transaction> returnedList = transactionService.getAllTransactions();

        assertEquals(savedList.size(), returnedList.size());
    }

    @Test
    void getPaginated() {

        List<Transaction> transactions = createTransactionList();

        //Create pageable
        Pageable pageable = PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE, Sort.by("date"));
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), transactions.size());

        //Create page
        Page<Transaction> pagedTransactions = new PageImpl<>(
                transactions.subList(start, end), pageable, transactions.size());


        when(transactionRepository.findAll(pageable)).thenReturn(pagedTransactions);

        Page<Transaction> returnedList = transactionService.getPaginated(DEFAULT_PAGE, DEFAULT_SIZE);

        assertEquals(pagedTransactions, returnedList);
    }

    @Test
    void getTransactionsByYear() {
        int year = 2023;

        List<Transaction> transactions = createTransactionList();

        Pageable pageable = PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE, Sort.by("date"));
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), transactions.size());

        Page<Transaction> pagedTransactions = new PageImpl<>(
                transactions.subList(start, end), pageable, transactions.size());

        LocalDateTime from = LocalDateTime.of(year, 1, 1, 0, 0, 0).withNano(0);
        LocalDateTime to = LocalDateTime.of(year, 12, 31, 23, 59, 59).withNano(0);


        when(transactionRepository.findByDateBetween(pageable, from, to)).thenReturn(pagedTransactions);

        Page<Transaction> returnedList = transactionService.getTransactionsByYear(DEFAULT_PAGE, DEFAULT_SIZE, year);

        assertEquals(pagedTransactions, returnedList);
    }

    @Test
    void getTransactionsByMonth() {
        int year = 2023;
        int month = 4;

        List<Transaction> transactions = createTransactionList();

        Pageable pageable = PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE, Sort.by("date"));
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), transactions.size());

        Page<Transaction> pagedTransactions = new PageImpl<>(
                transactions.subList(start, end), pageable, transactions.size());

        LocalDateTime from = LocalDateTime.of(year, month, 1, 0, 0, 0).withNano(0);
        LocalDateTime to = LocalDateTime.of(year, month, 30, 23, 59, 59).withNano(0);


        when(transactionRepository.findByDateBetween(pageable, from, to)).thenReturn(pagedTransactions);

        Page<Transaction> returnedList = transactionService.getTransactionsByMonth(DEFAULT_PAGE, DEFAULT_SIZE, year, month);

        assertEquals(pagedTransactions, returnedList);
    }

    @Test
    void getTransactionsByMonth_InvalidMonth() {
        int year = 2023;
        int month = 13; //invalid month

        assertThrows(InvalidMonthException.class,
                () ->  transactionService.getTransactionsByMonth(DEFAULT_PAGE, DEFAULT_SIZE, year, month));
    }

    @Test
    void getTransactionById() {
        Transaction savedEntity = createTransactionEntity();

        when(transactionRepository.findById(savedEntity.getId())).thenReturn(Optional.of(savedEntity));

        Transaction returnedEntity = transactionService.getTransactionById(savedEntity.getId());

        assertEquals(savedEntity.getId(), returnedEntity.getId());
        assertEquals(savedEntity.getAmount(), returnedEntity.getAmount());
        assertEquals(savedEntity.getDate(), returnedEntity.getDate());
        assertEquals(savedEntity.getDescription(), returnedEntity.getDescription());
        assertEquals(savedEntity.getCategory(), returnedEntity.getCategory());
        assertEquals(savedEntity.getPaymentMethod(), returnedEntity.getPaymentMethod());
        assertEquals(savedEntity.getVendor(), returnedEntity.getVendor());
        assertEquals(savedEntity.getTags(), returnedEntity.getTags());

    }

    @Test
    void getTransactionById_NotFound() {

        Integer notFoundId = 123;

        when(transactionRepository.findById(notFoundId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.getTransactionById(notFoundId));

    }


    // -------------- CREATE ----------------------------
    @Test
    void createNewTransaction() {

        Transaction passedEntity = new Transaction(AMOUNT, DATE, DESCRIPTION, CATEGORY,
                PAYMENT_METHOD, VENDOR_OS, Stream.of(TAG1, TAG2).collect(Collectors.toSet()));

        Transaction savedEntity = new Transaction(passedEntity.getAmount(), passedEntity.getDate(),
                passedEntity.getDescription(), passedEntity.getCategory(), passedEntity.getPaymentMethod(),
                passedEntity.getVendor(), passedEntity.getTags());
        savedEntity.setId(1);

        when(categoryService.getCategoryById(CATEGORY.getId())).thenReturn(CATEGORY);
        when(paymentMethodService.getPaymentMethodById(PAYMENT_METHOD.getId())).thenReturn(PAYMENT_METHOD);
        when(vendorService.getVendorById(VENDOR_OS.getId())).thenReturn(VENDOR_OS);
        when(tagService.getTagById(TAG1.getId())).thenReturn(TAG1);
        when(tagService.getTagById(TAG2.getId())).thenReturn(TAG2);
        when(transactionRepository.save(passedEntity)).thenReturn(savedEntity);

        Transaction returnedEntity = transactionService.createNewTransaction(passedEntity);

        assertEquals(savedEntity.getId(), returnedEntity.getId());
        assertEquals(passedEntity.getAmount(), returnedEntity.getAmount());
        assertEquals(passedEntity.getDate(), returnedEntity.getDate());
        assertEquals(passedEntity.getDescription(), returnedEntity.getDescription());
        assertEquals(passedEntity.getCategory(), returnedEntity.getCategory());
        assertEquals(passedEntity.getPaymentMethod(), returnedEntity.getPaymentMethod());
        assertEquals(passedEntity.getVendor(), returnedEntity.getVendor());
        assertEquals(passedEntity.getTags(), returnedEntity.getTags());

    }


    // -------------- UPDATE ----------------------------
    @Test
    void updateTransactionById() {

        Category updatedCategory = new Category("Updated Category", Color.BLUE);
        updatedCategory.setId(123);

        Transaction passedEntity = new Transaction(10.0, DATE, "Test Update", updatedCategory,
                PAYMENT_METHOD, VENDOR_PS, Stream.of(TAG2).collect(Collectors.toSet()));

        Transaction originalEntity = createTransactionEntity();

        Transaction updatedEntity = new Transaction(passedEntity.getAmount(), passedEntity.getDate(),
                passedEntity.getDescription(), passedEntity.getCategory(), passedEntity.getPaymentMethod(),
                passedEntity.getVendor(), passedEntity.getTags());
        updatedEntity.setId(originalEntity.getId());


        when(categoryService.getCategoryById(updatedCategory.getId())).thenReturn(updatedCategory);
        when(paymentMethodService.getPaymentMethodById(PAYMENT_METHOD.getId())).thenReturn(PAYMENT_METHOD);
        when(vendorService.getVendorById(VENDOR_PS.getId())).thenReturn(VENDOR_PS);
        when(tagService.getTagById(TAG2.getId())).thenReturn(TAG2);
        when(transactionRepository.findById(originalEntity.getId())).thenReturn(Optional.of(originalEntity));
        when(transactionRepository.save(updatedEntity)).thenReturn(updatedEntity);

        Transaction returnedEntity = transactionService.updateTransactionById(originalEntity.getId(), passedEntity);

        assertEquals(originalEntity.getId(), returnedEntity.getId());
        assertEquals(passedEntity.getAmount(), returnedEntity.getAmount());
        assertEquals(passedEntity.getDate(), returnedEntity.getDate());
        assertEquals(passedEntity.getDescription(), returnedEntity.getDescription());

        assertEquals(passedEntity.getCategory(), returnedEntity.getCategory());
        assertNotEquals(originalEntity.getCategory(), returnedEntity.getCategory());

        assertEquals(passedEntity.getPaymentMethod(), returnedEntity.getPaymentMethod());
        assertEquals(passedEntity.getVendor(), returnedEntity.getVendor());
        assertEquals(passedEntity.getTags(), returnedEntity.getTags());
    }

    @Test
    void updateTransactionById_NotFound() {

        Integer notFoundId = 123;

        Transaction passedEntity = new Transaction(10.0, DATE, "Test Update", new Category(),
                PAYMENT_METHOD, VENDOR_PS, Stream.of(TAG2).collect(Collectors.toSet()));

        when(transactionRepository.findById(notFoundId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.updateTransactionById(notFoundId, passedEntity));
    }


    // -------------- PATCH ----------------------------
    @Test
    void patchTransactionById() {

        Category modifiedCategory = new Category();
        modifiedCategory.setId(123);

        PaymentMethod modifiedPM = new PaymentMethod();
        modifiedPM.setId(123);

        Transaction passedEntity = new Transaction(10.0, LocalDateTime.now(), "Test Update",
                modifiedCategory, modifiedPM, null, Stream.of(TAG2).collect(Collectors.toSet()));

        Transaction originalEntity = createTransactionEntity();

        Transaction updatedEntity = new Transaction(passedEntity.getAmount(), passedEntity.getDate(),
                passedEntity.getDescription(), passedEntity.getCategory(), passedEntity.getPaymentMethod(),
                originalEntity.getVendor(), passedEntity.getTags());
        updatedEntity.setId(originalEntity.getId());

        when(categoryService.getCategoryById(modifiedCategory.getId())).thenReturn(modifiedCategory);
        when(paymentMethodService.getPaymentMethodById(modifiedPM.getId())).thenReturn(modifiedPM);
        when(tagService.getTagById(TAG2.getId())).thenReturn(TAG2);
        when(transactionRepository.findById(originalEntity.getId())).thenReturn(Optional.of(originalEntity));
        when(transactionRepository.save(updatedEntity)).thenReturn(updatedEntity);

        Transaction returnedEntity = transactionService.patchTransactionById(originalEntity.getId(), passedEntity);

        assertEquals(originalEntity.getId(), returnedEntity.getId());
        assertEquals(passedEntity.getAmount(), returnedEntity.getAmount());
        assertEquals(passedEntity.getDate(), returnedEntity.getDate());
        assertEquals(passedEntity.getDescription(), returnedEntity.getDescription());
        assertEquals(passedEntity.getCategory(), returnedEntity.getCategory());
        assertEquals(passedEntity.getPaymentMethod(), returnedEntity.getPaymentMethod());
        assertEquals(originalEntity.getVendor(), returnedEntity.getVendor());
        assertEquals(passedEntity.getTags(), returnedEntity.getTags());
    }

    @Test
    void patchTransactionById_UpdateOnlyAmount() {

        Transaction passedEntity = new Transaction();
        passedEntity.setAmount(10.0);

        Transaction originalEntity = createTransactionEntity();

        Transaction updatedEntity = new Transaction(passedEntity.getAmount(), originalEntity.getDate(),
                originalEntity.getDescription(), originalEntity.getCategory(), originalEntity.getPaymentMethod(),
                originalEntity.getVendor(), originalEntity.getTags());
        updatedEntity.setId(originalEntity.getId());

        when(transactionRepository.findById(originalEntity.getId())).thenReturn(Optional.of(originalEntity));
        when(transactionRepository.save(updatedEntity)).thenReturn(updatedEntity);

        Transaction returnedEntity = transactionService.patchTransactionById(originalEntity.getId(), passedEntity);

        assertEquals(originalEntity.getId(), returnedEntity.getId());
        assertEquals(passedEntity.getAmount(), returnedEntity.getAmount());
        assertEquals(originalEntity.getDate(), returnedEntity.getDate());
        assertEquals(originalEntity.getDescription(), returnedEntity.getDescription());
        assertEquals(originalEntity.getCategory(), returnedEntity.getCategory());
        assertEquals(originalEntity.getPaymentMethod(), returnedEntity.getPaymentMethod());
        assertEquals(originalEntity.getVendor(), returnedEntity.getVendor());
        assertEquals(originalEntity.getTags(), returnedEntity.getTags());
    }

    @Test
    void patchTransactionById_UpdateOnlyData() {

        LocalDateTime newDate = LocalDateTime.of(2023, 4, 24, 8, 30, 0).withNano(0);

        Transaction passedEntity = new Transaction();
        passedEntity.setDate(newDate);

        Transaction originalEntity = createTransactionEntity();

        Transaction updatedEntity = new Transaction(originalEntity.getAmount(), passedEntity.getDate(),
                originalEntity.getDescription(), originalEntity.getCategory(), originalEntity.getPaymentMethod(),
                originalEntity.getVendor(), originalEntity.getTags());
        updatedEntity.setId(originalEntity.getId());

        when(transactionRepository.findById(originalEntity.getId())).thenReturn(Optional.of(originalEntity));
        when(transactionRepository.save(updatedEntity)).thenReturn(updatedEntity);

        Transaction returnedEntity = transactionService.patchTransactionById(originalEntity.getId(), passedEntity);

        assertEquals(originalEntity.getId(), returnedEntity.getId());
        assertEquals(originalEntity.getAmount(), returnedEntity.getAmount());
        assertEquals(passedEntity.getDate(), returnedEntity.getDate());
        assertEquals(originalEntity.getDescription(), returnedEntity.getDescription());
        assertEquals(originalEntity.getCategory(), returnedEntity.getCategory());
        assertEquals(originalEntity.getPaymentMethod(), returnedEntity.getPaymentMethod());
        assertEquals(originalEntity.getVendor(), returnedEntity.getVendor());
        assertEquals(originalEntity.getTags(), returnedEntity.getTags());
    }

    @Test
    void patchTransactionById_UpdateOnlyDescription() {

        Transaction passedEntity = new Transaction();
        passedEntity.setDescription("New Description");

        Transaction originalEntity = createTransactionEntity();

        Transaction updatedEntity = new Transaction(originalEntity.getAmount(), originalEntity.getDate(),
                passedEntity.getDescription(), originalEntity.getCategory(), originalEntity.getPaymentMethod(),
                originalEntity.getVendor(), originalEntity.getTags());
        updatedEntity.setId(originalEntity.getId());

        when(transactionRepository.findById(originalEntity.getId())).thenReturn(Optional.of(originalEntity));
        when(transactionRepository.save(updatedEntity)).thenReturn(updatedEntity);

        Transaction returnedEntity = transactionService.patchTransactionById(originalEntity.getId(), passedEntity);

        assertEquals(originalEntity.getId(), returnedEntity.getId());
        assertEquals(originalEntity.getAmount(), returnedEntity.getAmount());
        assertEquals(originalEntity.getDate(), returnedEntity.getDate());
        assertEquals(passedEntity.getDescription(), returnedEntity.getDescription());
        assertEquals(originalEntity.getCategory(), returnedEntity.getCategory());
        assertEquals(originalEntity.getPaymentMethod(), returnedEntity.getPaymentMethod());
        assertEquals(originalEntity.getVendor(), returnedEntity.getVendor());
        assertEquals(originalEntity.getTags(), returnedEntity.getTags());
    }

    @Test
    void patchTransactionById_UpdateOnlyCategory() {

        Category modifiedCategory = new Category();
        modifiedCategory.setId(123);

        Transaction passedEntity = new Transaction();
        passedEntity.setCategory(modifiedCategory);

        Transaction originalEntity = createTransactionEntity();

        Transaction updatedEntity = new Transaction(originalEntity.getAmount(), originalEntity.getDate(),
                originalEntity.getDescription(), passedEntity.getCategory(), originalEntity.getPaymentMethod(),
                originalEntity.getVendor(), originalEntity.getTags());
        updatedEntity.setId(originalEntity.getId());

        when(categoryService.getCategoryById(modifiedCategory.getId())).thenReturn(modifiedCategory);
        when(transactionRepository.findById(originalEntity.getId())).thenReturn(Optional.of(originalEntity));
        when(transactionRepository.save(updatedEntity)).thenReturn(updatedEntity);

        Transaction returnedEntity = transactionService.patchTransactionById(originalEntity.getId(), passedEntity);

        assertEquals(originalEntity.getId(), returnedEntity.getId());
        assertEquals(originalEntity.getAmount(), returnedEntity.getAmount());
        assertEquals(originalEntity.getDate(), returnedEntity.getDate());
        assertEquals(originalEntity.getDescription(), returnedEntity.getDescription());
        assertEquals(passedEntity.getCategory(), returnedEntity.getCategory());
        assertEquals(originalEntity.getPaymentMethod(), returnedEntity.getPaymentMethod());
        assertEquals(originalEntity.getVendor(), returnedEntity.getVendor());
        assertEquals(originalEntity.getTags(), returnedEntity.getTags());

        verify(transactionRepository, times(1)).save(updatedEntity);
    }

    @Test
    void patchTransactionById_UpdateOnlyPaymentMethod() {

        PaymentMethod modifiedPaymentMethod = new PaymentMethod();
        modifiedPaymentMethod.setId(123);

        Transaction passedEntity = new Transaction();
        passedEntity.setPaymentMethod(modifiedPaymentMethod);

        Transaction originalEntity = createTransactionEntity();

        Transaction updatedEntity = new Transaction(originalEntity.getAmount(), originalEntity.getDate(),
                originalEntity.getDescription(), originalEntity.getCategory(), passedEntity.getPaymentMethod(),
                originalEntity.getVendor(), originalEntity.getTags());
        updatedEntity.setId(originalEntity.getId());

        when(paymentMethodService.getPaymentMethodById(modifiedPaymentMethod.getId())).thenReturn(modifiedPaymentMethod);
        when(transactionRepository.findById(originalEntity.getId())).thenReturn(Optional.of(originalEntity));
        when(transactionRepository.save(updatedEntity)).thenReturn(updatedEntity);

        Transaction returnedEntity = transactionService.patchTransactionById(originalEntity.getId(), passedEntity);

        assertEquals(originalEntity.getId(), returnedEntity.getId());
        assertEquals(originalEntity.getAmount(), returnedEntity.getAmount());
        assertEquals(originalEntity.getDate(), returnedEntity.getDate());
        assertEquals(originalEntity.getDescription(), returnedEntity.getDescription());
        assertEquals(originalEntity.getCategory(), returnedEntity.getCategory());
        assertEquals(passedEntity.getPaymentMethod(), returnedEntity.getPaymentMethod());
        assertEquals(originalEntity.getVendor(), returnedEntity.getVendor());
        assertEquals(originalEntity.getTags(), returnedEntity.getTags());

        verify(transactionRepository, times(1)).save(updatedEntity);
    }

    @Test
    void patchTransactionById_UpdateOnlyVendor() {

        Transaction passedEntity = new Transaction();
        passedEntity.setVendor(VENDOR_PS);

        Transaction originalEntity = createTransactionEntity();

        Transaction updatedEntity = new Transaction(originalEntity.getAmount(), originalEntity.getDate(),
                originalEntity.getDescription(), originalEntity.getCategory(), originalEntity.getPaymentMethod(),
                passedEntity.getVendor(), originalEntity.getTags());
        updatedEntity.setId(originalEntity.getId());

        when(vendorService.getVendorById(passedEntity.getVendor().getId())).thenReturn(VENDOR_PS);
        when(transactionRepository.findById(originalEntity.getId())).thenReturn(Optional.of(originalEntity));
        when(transactionRepository.save(updatedEntity)).thenReturn(updatedEntity);

        Transaction returnedEntity = transactionService.patchTransactionById(originalEntity.getId(), passedEntity);

        assertEquals(originalEntity.getId(), returnedEntity.getId());
        assertEquals(originalEntity.getAmount(), returnedEntity.getAmount());
        assertEquals(originalEntity.getDate(), returnedEntity.getDate());
        assertEquals(originalEntity.getDescription(), returnedEntity.getDescription());
        assertEquals(originalEntity.getCategory(), returnedEntity.getCategory());
        assertEquals(originalEntity.getPaymentMethod(), returnedEntity.getPaymentMethod());
        assertEquals(passedEntity.getVendor(), returnedEntity.getVendor());
        assertEquals(originalEntity.getTags(), returnedEntity.getTags());

        verify(transactionRepository, times(1)).save(updatedEntity);
    }

    @Test
    void patchTransactionById_UpdateOnlyTags() {

        Transaction passedEntity = new Transaction();
        passedEntity.setTags(Stream.of(TAG1).collect(Collectors.toSet()));

        Transaction originalEntity = createTransactionEntity();

        Transaction updatedEntity = new Transaction(originalEntity.getAmount(), originalEntity.getDate(),
                originalEntity.getDescription(), originalEntity.getCategory(), originalEntity.getPaymentMethod(),
                originalEntity.getVendor(), passedEntity.getTags());
        updatedEntity.setId(originalEntity.getId());

        when(tagService.getTagById(TAG1.getId())).thenReturn(TAG1);
        when(transactionRepository.findById(originalEntity.getId())).thenReturn(Optional.of(originalEntity));
        when(transactionRepository.save(updatedEntity)).thenReturn(updatedEntity);

        Transaction returnedEntity = transactionService.patchTransactionById(originalEntity.getId(), passedEntity);

        assertEquals(originalEntity.getId(), returnedEntity.getId());
        assertEquals(originalEntity.getAmount(), returnedEntity.getAmount());
        assertEquals(originalEntity.getDate(), returnedEntity.getDate());
        assertEquals(originalEntity.getDescription(), returnedEntity.getDescription());
        assertEquals(originalEntity.getCategory(), returnedEntity.getCategory());
        assertEquals(originalEntity.getPaymentMethod(), returnedEntity.getPaymentMethod());
        assertEquals(originalEntity.getVendor(), returnedEntity.getVendor());
        assertEquals(passedEntity.getTags(), returnedEntity.getTags());

        verify(transactionRepository, times(1)).save(updatedEntity);
    }

    @Test
    void patchTransactionById_IdNotFound() {

        Integer notFoundId = 123;

        Transaction passedEntity = new Transaction(10.0, LocalDateTime.now(), "Test Update",
                CATEGORY, PAYMENT_METHOD, VENDOR_PS, Stream.of(TAG2).collect(Collectors.toSet()));


        when(transactionRepository.findById(notFoundId)).thenReturn(Optional.empty());


        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.patchTransactionById(notFoundId, passedEntity));
    }


    // -------------- DELETE ----------------------------
    @Test
    void deleteTransactionById() {
        Integer ID = 1;

        when(transactionRepository.findById(ID)).thenReturn(Optional.of(new Transaction()));
        doNothing().when(transactionRepository).deleteById(ID);

        transactionService.deleteTransactionById(ID);

        verify(transactionRepository, times(1)).deleteById(ID);
    }

    @Test
    void deleteTransactionById_NotFound() {
        Integer ID = 123;

        when(transactionRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.deleteTransactionById(ID));
    }


    // -------------- Helpers ----------------------------
    private static Transaction createTransactionEntity() {
        Transaction entity = new Transaction(AMOUNT, DATE, DESCRIPTION, CATEGORY,
                PAYMENT_METHOD, VENDOR_OS, Stream.of(TAG1, TAG2).collect(Collectors.toSet()));
        entity.setId(1);
        return entity;
    }

    private static List<Transaction> createTransactionList() {
        Transaction t1 = new Transaction(AMOUNT, DATE, DESCRIPTION, CATEGORY,
                PAYMENT_METHOD, VENDOR_OS, Stream.of(TAG1, TAG2).collect(Collectors.toSet()));
        t1.setId(1);

        Transaction t2 = new Transaction(AMOUNT, DATE, DESCRIPTION, CATEGORY,
                PAYMENT_METHOD, VENDOR_PS, Stream.of(TAG1).collect(Collectors.toSet()));
        t2.setId(2);

        return Arrays.asList(t1, t2);
    }
}