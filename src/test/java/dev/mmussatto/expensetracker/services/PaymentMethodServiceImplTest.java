/*
 * Created by murilo.mussatto on 03/03/2023
 */

package dev.mmussatto.expensetracker.services;

import dev.mmussatto.expensetracker.domain.PaymentMethod;
import dev.mmussatto.expensetracker.domain.PaymentType;
import dev.mmussatto.expensetracker.domain.Transaction;
import dev.mmussatto.expensetracker.repositories.PaymentMethodRepository;
import dev.mmussatto.expensetracker.services.exceptions.ResourceAlreadyExistsException;
import dev.mmussatto.expensetracker.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentMethodServiceImplTest {

    @Mock
    PaymentMethodRepository paymentMethodRepository;

    @InjectMocks
    PaymentMethodServiceImpl paymentMethodService;

    public static final Integer ID = 1;
    public static final String NAME = "Test";
    public static final PaymentType TYPE = PaymentType.CREDIT_CARD;
    public static final Transaction TRANSACTION = new Transaction();

    @BeforeAll
    static void initializeTransaction() {
        TRANSACTION.setId(1);
    }

    @Test
    void getAllPaymentMethods() {

        PaymentMethod p1 = new PaymentMethod();
        p1.setId(1);

        PaymentMethod p2 = new PaymentMethod();
        p2.setId(2);

        List<PaymentMethod> paymentMethods = Arrays.asList(p1, p2);

        when(paymentMethodRepository.findAll()).thenReturn(paymentMethods);

        List<PaymentMethod> returnedList = paymentMethodService.getAllPaymentMethods();

        assertEquals(paymentMethods.size(), returnedList.size());
    }

    @Test
    void getPaymentMethodById() {

        PaymentMethod savedEntity = createPaymentMethodEntity();

        when(paymentMethodRepository.findById(savedEntity.getId())).thenReturn(Optional.of(savedEntity));

        PaymentMethod returnedEntity = paymentMethodService.getPaymentMethodById(savedEntity.getId());

        assertEquals(savedEntity.getId(),returnedEntity.getId());
        assertEquals(savedEntity.getName(),returnedEntity.getName());
        assertEquals(savedEntity.getType(),returnedEntity.getType());
        assertEquals(savedEntity.getTransactions(),returnedEntity.getTransactions());
    }

    @Test
    void getPaymentMethodById_NotFound() {

        when(paymentMethodRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentMethodService.getPaymentMethodById(ID));
    }

    @Test
    void getPaymentMethodByName() {

        PaymentMethod savedEntity = createPaymentMethodEntity();

        when(paymentMethodRepository.findByName(savedEntity.getName())).thenReturn(Optional.of(savedEntity));

        PaymentMethod returnedEntity = paymentMethodService.getPaymentMethodByName(savedEntity.getName());

        assertEquals(savedEntity.getId(),returnedEntity.getId());
        assertEquals(savedEntity.getName(),returnedEntity.getName());
        assertEquals(savedEntity.getType(),returnedEntity.getType());
        assertEquals(savedEntity.getTransactions(),returnedEntity.getTransactions());
    }

    @Test
    void getPaymentMethodByName_NotFound() {

        when(paymentMethodRepository.findByName(NAME)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentMethodService.getPaymentMethodByName(NAME));

    }

    @Test
    void createNewPaymentMethod() {

        //Entity passed to function
        PaymentMethod passedEntity = new PaymentMethod(NAME, TYPE);
        passedEntity.getTransactions().add(TRANSACTION);

        //Saved Entity
        PaymentMethod savedEntity = new PaymentMethod(passedEntity.getName(), passedEntity.getType());
        savedEntity.setId(ID);
        savedEntity.setTransactions(passedEntity.getTransactions());


        when(paymentMethodRepository.save(passedEntity)).thenReturn(savedEntity);

        PaymentMethod returnedEntity = paymentMethodService.createNewPaymentMethod(passedEntity);

        assertEquals(savedEntity.getId(), returnedEntity.getId());
        assertEquals(passedEntity.getName(), returnedEntity.getName());
        assertEquals(passedEntity.getType(), returnedEntity.getType());
        assertEquals(passedEntity.getTransactions(),returnedEntity.getTransactions());
    }

    @Test
    void createNewPaymentMethod_NameAlreadyExists() {

        PaymentMethod passedEntity = createPaymentMethodEntity();

        //When searching the repository by name, find an item
        when(paymentMethodRepository.findByName(passedEntity.getName())).thenReturn(Optional.of(passedEntity));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> paymentMethodService.createNewPaymentMethod(passedEntity));
    }

    @Test
    void updatePaymentMethodById() {

        //Entity passed to updatePaymentMethodById
        PaymentMethod passedEntity = new PaymentMethod("TestUpdate", PaymentType.CASH);
        passedEntity.getTransactions().add(TRANSACTION);

        //Original PaymentMethod
        PaymentMethod originalEntity = new PaymentMethod(NAME, TYPE);
        originalEntity.setId(ID);

        //Updated Payment Method
        PaymentMethod updatedEntity = new PaymentMethod(passedEntity.getName(), passedEntity.getType());
        updatedEntity.setId(originalEntity.getId());
        updatedEntity.setTransactions(passedEntity.getTransactions());

        when(paymentMethodRepository.findById(originalEntity.getId())).thenReturn(Optional.of(originalEntity));
        when(paymentMethodRepository.save(updatedEntity)).thenReturn(updatedEntity);

        PaymentMethod returnedEntity = paymentMethodService.updatePaymentMethodById(ID, passedEntity);

        assertEquals(originalEntity.getId(), returnedEntity.getId());        //same id
        assertEquals(passedEntity.getName(), returnedEntity.getName());   //updated name
        assertEquals(passedEntity.getType(), returnedEntity.getType());   //updated type
        assertEquals(passedEntity.getTransactions(),returnedEntity.getTransactions());    //updated transaction

        verify(paymentMethodRepository, times(1)).save(updatedEntity);
    }

    @Test
    void updatePaymentMethodById_NotFound() {

        //Entity passed to updatePaymentMethodById
        PaymentMethod passedEntity = new PaymentMethod("TestUpdate", PaymentType.CASH);
        passedEntity.getTransactions().add(TRANSACTION);

        //Original PaymentMethod
        PaymentMethod originalEntity = new PaymentMethod(NAME, TYPE);
        originalEntity.setId(ID);


        when(paymentMethodRepository.findById(originalEntity.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> paymentMethodService.updatePaymentMethodById(originalEntity.getId(), passedEntity));
    }

    @Test
    void updatePaymentMethodById_NameAlreadyExists() {

        //Entity passed to updatePaymentMethodById
        PaymentMethod passedEntity = new PaymentMethod("TestUpdate", PaymentType.CASH);
        passedEntity.getTransactions().add(TRANSACTION);

        //Original PaymentMethod
        PaymentMethod original = new PaymentMethod(NAME, TYPE);
        original.setId(ID);

        //Original PaymentMethod
        PaymentMethod nameAlreadyInUse = new PaymentMethod(passedEntity.getName(), TYPE);

        when(paymentMethodRepository.findById(original.getId())).thenReturn(Optional.of(original));
        when(paymentMethodRepository.findByName(passedEntity.getName())).thenReturn(Optional.of(nameAlreadyInUse));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> paymentMethodService.updatePaymentMethodById(original.getId(), passedEntity));
    }

    @Test
    void patchPaymentMethodById() {

        //Entity passed to patchPaymentMethodById
        PaymentMethod passedEntity = new PaymentMethod("Test Patch", PaymentType.CASH);
        passedEntity.getTransactions().add(TRANSACTION);

        //Original PaymentMethod
        PaymentMethod originalEntity = new PaymentMethod(NAME, TYPE);
        originalEntity.setId(ID);

        //Updated Payment Method
        PaymentMethod updated = new PaymentMethod(passedEntity.getName(), passedEntity.getType());
        updated.setId(originalEntity.getId());
        updated.setTransactions(passedEntity.getTransactions());

        when(paymentMethodRepository.findById(originalEntity.getId())).thenReturn(Optional.of(originalEntity));
        when(paymentMethodRepository.findByName(passedEntity.getName())).thenReturn(Optional.empty());
        when(paymentMethodRepository.save(updated)).thenReturn(updated);


        PaymentMethod returnedEntity = paymentMethodService.patchPaymentMethodById(originalEntity.getId(), passedEntity);


        assertEquals(originalEntity.getId(), returnedEntity.getId());       //same id
        assertEquals(passedEntity.getName(), returnedEntity.getName());     //updated name
        assertEquals(passedEntity.getType(), returnedEntity.getType());     //updated type
        assertEquals(passedEntity.getTransactions(),returnedEntity.getTransactions()); //updated transaction


        verify(paymentMethodRepository, times(1)).save(updated);
    }

    @Test
    void patchPaymentMethodById_UpdateOnlyName() {

        //Entity passed to patchPaymentMethodById
        PaymentMethod passedEntity = new PaymentMethod();
        passedEntity.setName("Test Patch");

        //Original PaymentMethod
        PaymentMethod original = createPaymentMethodEntity();

        //Updated Payment Method
        PaymentMethod updated = new PaymentMethod(passedEntity.getName(), original.getType());
        updated.setId(original.getId());
        updated.setTransactions(original.getTransactions());

        when(paymentMethodRepository.findById(original.getId())).thenReturn(Optional.of(original));
        when(paymentMethodRepository.findByName(passedEntity.getName())).thenReturn(Optional.empty());
        when(paymentMethodRepository.save(updated)).thenReturn(updated);

        PaymentMethod returnedEntity = paymentMethodService.patchPaymentMethodById(original.getId(), passedEntity);

        assertEquals(original.getId(), returnedEntity.getId());             //same id
        assertEquals(passedEntity.getName(), returnedEntity.getName());     //updated name
        assertEquals(original.getType(), returnedEntity.getType());         //same type
        assertEquals(original.getTransactions(),returnedEntity.getTransactions());  //same transactions

        verify(paymentMethodRepository, times(1)).save(updated);
    }

    @Test
    void patchPaymentMethodById_UpdateOnlyType() {

        //Entity passed to patchPaymentMethodById
        PaymentMethod passedEntity = new PaymentMethod();
        passedEntity.setType(PaymentType.CASH);

        //Original PaymentMethod
        PaymentMethod original = createPaymentMethodEntity();

        //Updated Payment Method
        PaymentMethod updated = new PaymentMethod(original.getName(), passedEntity.getType());
        updated.setId(original.getId());
        updated.setTransactions(original.getTransactions());

        when(paymentMethodRepository.findById(original.getId())).thenReturn(Optional.of(original));
        when(paymentMethodRepository.save(updated)).thenReturn(updated);

        PaymentMethod returnedEntity = paymentMethodService.patchPaymentMethodById(original.getId(), passedEntity);

        assertEquals(original.getId(), returnedEntity.getId());         //same id
        assertEquals(original.getName(), returnedEntity.getName());     //same name
        assertEquals(passedEntity.getType(), returnedEntity.getType()); //updated type
        assertEquals(original.getTransactions(),returnedEntity.getTransactions());  //same transaction

        verify(paymentMethodRepository, times(1)).save(updated);
    }

    @Test
    void patchPaymentMethodById_UpdateOnlyTransactions() {

        //Entity passed to patchPaymentMethodById
        PaymentMethod passedEntity = new PaymentMethod();
        passedEntity.getTransactions().add(TRANSACTION);

        //Original PaymentMethod
        PaymentMethod original = new PaymentMethod(NAME, TYPE);
        original.setId(ID);

        //Updated Payment Method
        PaymentMethod updated = new PaymentMethod(original.getName(), original.getType());
        updated.setId(original.getId());
        updated.setTransactions(passedEntity.getTransactions());

        when(paymentMethodRepository.findById(original.getId())).thenReturn(Optional.of(original));
        when(paymentMethodRepository.save(updated)).thenReturn(updated);

        PaymentMethod returnedEntity = paymentMethodService.patchPaymentMethodById(original.getId(), passedEntity);

        assertEquals(original.getId(), returnedEntity.getId());         //same id
        assertEquals(original.getName(), returnedEntity.getName());     //same name
        assertEquals(original.getType(), returnedEntity.getType());     //same type
        assertEquals(passedEntity.getTransactions(),returnedEntity.getTransactions()); //updated transactions


        verify(paymentMethodRepository, times(1)).save(updated);
    }

    @Test
    void patchPaymentMethodById_NotFound() {

        Integer notFoundId = 123;

        //Entity passed to patchPaymentMethodById
        PaymentMethod passedEntity = new PaymentMethod();

        when(paymentMethodRepository.findById(notFoundId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> paymentMethodService.patchPaymentMethodById(notFoundId, passedEntity));
    }

    @Test
    void patchPaymentMethodById_NameAlreadyExists() {

        //Entity passed to updatePaymentMethodById
        PaymentMethod passedEntity = new PaymentMethod("Test Patch", PaymentType.CASH);
        passedEntity.getTransactions().add(TRANSACTION);

        //Original PaymentMethod
        PaymentMethod original = new PaymentMethod(NAME, TYPE);
        original.setId(ID);

        //Original PaymentMethod
        PaymentMethod nameAlreadyInUse = new PaymentMethod(passedEntity.getName(), TYPE);

        when(paymentMethodRepository.findById(original.getId())).thenReturn(Optional.of(original));
        when(paymentMethodRepository.findByName(passedEntity.getName())).thenReturn(Optional.of(nameAlreadyInUse));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> paymentMethodService.patchPaymentMethodById(original.getId(), passedEntity));
    }


    @Test
    void deletePaymentMethodById() {

        when(paymentMethodRepository.findById(ID)).thenReturn(Optional.of(new PaymentMethod()));
        doNothing().when(paymentMethodRepository).deleteById(ID);

        paymentMethodService.deletePaymentMethodById(ID);

        verify(paymentMethodRepository, times(1)).deleteById(ID);
    }

    @Test
    void deletePaymentMethodById_NotFound() {

        when(paymentMethodRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentMethodService.deletePaymentMethodById(ID));

    }

    @Test
    void getPaymentMethodTransactionsById() {

        Transaction t1 = new Transaction();
        t1.setId(1);
        t1.setAmount(53.00);
        t1.setDescription("Test Transaction 1");

        Transaction t2 = new Transaction();
        t2.setId(2);
        t2.setAmount(123.00);
        t2.setDescription("Test Transaction 2");

        Set<Transaction> transactions = new HashSet<>(Arrays.asList(t1, t2));

        PaymentMethod paymentMethod = new PaymentMethod(NAME, TYPE);
        paymentMethod.setId(ID);
        t1.setPaymentMethod(paymentMethod);
        t2.setPaymentMethod(paymentMethod);
        paymentMethod.setTransactions(transactions);

        when(paymentMethodRepository.findById(paymentMethod.getId())).thenReturn(Optional.of(paymentMethod));

        Set<Transaction> returnedSet = paymentMethodService.getPaymentMethodTransactionsById(paymentMethod.getId());

        assertEquals(transactions, returnedSet);
    }

    @Test
    void getPaymentMethodTransactionsById_NotFound() {

        Integer notFoundId = 123;

        when(paymentMethodRepository.findById(notFoundId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> paymentMethodService.getPaymentMethodTransactionsById(notFoundId));
    }


    private static PaymentMethod createPaymentMethodEntity() {
        PaymentMethod entity = new PaymentMethod(NAME, TYPE);
        entity.setId(ID);
        entity.getTransactions().add(TRANSACTION);
        return entity;
    }
}