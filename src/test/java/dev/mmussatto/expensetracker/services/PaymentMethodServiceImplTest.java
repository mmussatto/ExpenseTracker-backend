/*
 * Created by murilo.mussatto on 03/03/2023
 */

package dev.mmussatto.expensetracker.services;

import dev.mmussatto.expensetracker.api.mappers.PaymentMethodMapper;
import dev.mmussatto.expensetracker.api.model.PaymentMethodDTO;
import dev.mmussatto.expensetracker.domain.PaymentMethod;
import dev.mmussatto.expensetracker.domain.PaymentType;
import dev.mmussatto.expensetracker.domain.Transaction;
import dev.mmussatto.expensetracker.repositories.PaymentMethodRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentMethodServiceImplTest {

    @Mock
    PaymentMethodRepository paymentMethodRepository;

    PaymentMethodService paymentMethodService;

    public static final Integer ID = 1;
    public static final String NAME = "Test";
    public static final PaymentType TYPE = PaymentType.CREDIT_CARD;
    public static final Transaction TRANSACTION = new Transaction();

    @BeforeEach
    void setUp() {
        paymentMethodService = new PaymentMethodServiceImpl(PaymentMethodMapper.INSTANCE, paymentMethodRepository);
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

        List<PaymentMethodDTO> returnedList = paymentMethodService.getAllPaymentMethods();

        assertEquals(paymentMethods.size(), returnedList.size());
        assertEquals("/api/payment-methods/" + p1.getId(),returnedList.get(0).getPath());
        assertEquals("/api/payment-methods/" + p2.getId(),returnedList.get(1).getPath());
    }

    @Test
    void getPaymentMethodById() {

        PaymentMethod paymentMethod = new PaymentMethod(NAME, TYPE);
        paymentMethod.setId(ID);
        paymentMethod.getTransactions().add(TRANSACTION);

        when(paymentMethodRepository.findById(paymentMethod.getId())).thenReturn(Optional.of(paymentMethod));

        PaymentMethodDTO paymentMethodDTO = paymentMethodService.getPaymentMethodById(ID);

        assertEquals(paymentMethod.getId(),paymentMethodDTO.getId());
        assertEquals(paymentMethod.getName(),paymentMethodDTO.getName());
        assertEquals(paymentMethod.getType(),paymentMethodDTO.getType());
        assertEquals(paymentMethod.getTransactions(),paymentMethodDTO.getTransactions());
        assertEquals("/api/payment-methods/" + paymentMethod.getId(),paymentMethodDTO.getPath());
    }

    @Test
    void getPaymentMethodById_NotFound() {

        when(paymentMethodRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentMethodService.getPaymentMethodById(ID));
    }

    @Test
    void getPaymentMethodByName() {

        PaymentMethod paymentMethod = new PaymentMethod(NAME, TYPE);
        paymentMethod.setId(ID);
        paymentMethod.getTransactions().add(TRANSACTION);

        when(paymentMethodRepository.findByName(paymentMethod.getName())).thenReturn(Optional.of(paymentMethod));

        PaymentMethodDTO paymentMethodDTO = paymentMethodService.getPaymentMethodByName(NAME);

        assertEquals(paymentMethod.getId(),paymentMethodDTO.getId());
        assertEquals(paymentMethod.getName(),paymentMethodDTO.getName());
        assertEquals(paymentMethod.getType(),paymentMethodDTO.getType());
        assertEquals(paymentMethod.getTransactions(),paymentMethodDTO.getTransactions());
        assertEquals("/api/payment-methods/" + paymentMethod.getId(),paymentMethodDTO.getPath());
    }

    @Test
    void getPaymentMethodByName_NotFound() {

        when(paymentMethodRepository.findByName(NAME)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentMethodService.getPaymentMethodByName(NAME));

    }

    @Test
    void createNewPaymentMethod() {

        //DTO passed to function
        PaymentMethodDTO paymentMethodDTO = new PaymentMethodDTO(NAME, TYPE);
        paymentMethodDTO.getTransactions().add(TRANSACTION);

        //Saved Entity
        PaymentMethod paymentMethod = new PaymentMethod(paymentMethodDTO.getName(), paymentMethodDTO.getType());
        paymentMethod.setId(ID);
        paymentMethod.setTransactions(paymentMethodDTO.getTransactions());

        //Check if entity is saved correctly
        ArgumentMatcher<PaymentMethod> argumentMatcher = paymentMethodToSave ->
                Objects.equals(paymentMethodToSave.getName(), paymentMethod.getName())
                && Objects.equals(paymentMethodToSave.getType(), paymentMethod.getType())
                && Objects.equals(paymentMethodToSave.getTransactions(), paymentMethod.getTransactions());


        when(paymentMethodRepository.save(argThat(argumentMatcher))).thenReturn(paymentMethod);


        PaymentMethodDTO returnedDTO = paymentMethodService.createNewPaymentMethod(paymentMethodDTO);

        assertEquals(paymentMethod.getId(), returnedDTO.getId());
        assertEquals(paymentMethod.getName(), returnedDTO.getName());
        assertEquals(paymentMethod.getType(), returnedDTO.getType());
        assertEquals(paymentMethod.getTransactions(),returnedDTO.getTransactions());
        assertEquals("/api/payment-methods/" + paymentMethod.getId(), returnedDTO.getPath());
    }

    @Test
    void createNewPaymentMethod_NameAlreadyExists() {

        PaymentMethod paymentMethod = new PaymentMethod(NAME, TYPE);
        paymentMethod.setId(ID);
        paymentMethod.getTransactions().add(TRANSACTION);

        PaymentMethodDTO paymentMethodDTO = new PaymentMethodDTO(NAME, TYPE);
        paymentMethod.getTransactions().add(TRANSACTION);

        //When searching the repository by name, find an item
        when(paymentMethodRepository.findByName(paymentMethodDTO.getName())).thenReturn(Optional.of(paymentMethod));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> paymentMethodService.createNewPaymentMethod(paymentMethodDTO));
    }

    @Test
    void createNewPaymentMethod_IdAlreadyExists() {

        PaymentMethod paymentMethod = new PaymentMethod(NAME, TYPE);
        paymentMethod.setId(ID);
        paymentMethod.getTransactions().add(TRANSACTION);

        PaymentMethodDTO paymentMethodDTO = new PaymentMethodDTO(NAME, TYPE);
        paymentMethodDTO.setId(ID);
        paymentMethod.getTransactions().add(TRANSACTION);

        //When searching the repository by id, find an item
        when(paymentMethodRepository.findById(paymentMethodDTO.getId())).thenReturn(Optional.of(paymentMethod));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> paymentMethodService.createNewPaymentMethod(paymentMethodDTO));
    }

    @Test
    void updatePaymentMethodById() {

        //DTO passed to updatePaymentMethodById
        PaymentMethodDTO passedDTO = new PaymentMethodDTO("TestUpdate", PaymentType.CASH);
        passedDTO.getTransactions().add(TRANSACTION);

        //Original PaymentMethod
        PaymentMethod original = new PaymentMethod(NAME, TYPE);
        original.setId(ID);

        //Updated Payment Method
        PaymentMethod updated = new PaymentMethod(passedDTO.getName(), passedDTO.getType());
        updated.setId(original.getId());
        updated.setTransactions(passedDTO.getTransactions());

        when(paymentMethodRepository.findById(original.getId())).thenReturn(Optional.of(original));
        when(paymentMethodRepository.save(updated)).thenReturn(updated);

        PaymentMethodDTO returnedDTO = paymentMethodService.updatePaymentMethodById(original.getId(), passedDTO);

        assertEquals(original.getId(), returnedDTO.getId());        //same id
        assertEquals(passedDTO.getName(), returnedDTO.getName());   //updated name
        assertEquals(passedDTO.getType(), returnedDTO.getType());   //updated type
        assertEquals(passedDTO.getTransactions(),returnedDTO.getTransactions());    //updated transaction
        assertEquals("/api/payment-methods/" + original.getId(), returnedDTO.getPath());

        verify(paymentMethodRepository, times(1)).save(updated);
    }

    @Test
    void updatePaymentMethodById_NotFound() {

        //DTO passed to updatePaymentMethodById
        PaymentMethodDTO passedDTO = new PaymentMethodDTO("TestUpdate", PaymentType.CASH);
        passedDTO.getTransactions().add(TRANSACTION);

        //Original PaymentMethod
        PaymentMethod original = new PaymentMethod(NAME, TYPE);
        original.setId(ID);


        when(paymentMethodRepository.findById(original.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> paymentMethodService.updatePaymentMethodById(original.getId(), passedDTO));
    }

    @Test
    void updatePaymentMethodById_InvalidIdModification() {

        //DTO passed to updatePaymentMethodById
        PaymentMethodDTO passedDTO = new PaymentMethodDTO("TestUpdate", PaymentType.CASH);
        passedDTO.getTransactions().add(TRANSACTION);
        passedDTO.setId(15); //attempting to change id in update

        //Original PaymentMethod
        PaymentMethod original = new PaymentMethod(NAME, TYPE);
        original.setId(ID);


        when(paymentMethodRepository.findById(original.getId())).thenReturn(Optional.of(original));

        assertThrows(InvalidIdModificationException.class,
                () -> paymentMethodService.updatePaymentMethodById(original.getId(), passedDTO));
    }

    @Test
    void patchPaymentMethodById() {

        //DTO passed to patchPaymentMethodById
        PaymentMethodDTO passedDTO = new PaymentMethodDTO("TestUpdate", PaymentType.CASH);
        passedDTO.getTransactions().add(TRANSACTION);

        //Original PaymentMethod
        PaymentMethod original = new PaymentMethod(NAME, TYPE);
        original.setId(ID);

        //Updated Payment Method
        PaymentMethod updated = new PaymentMethod(original.getName(), original.getType());
        updated.setId(original.getId());

        when(paymentMethodRepository.findById(original.getId())).thenReturn(Optional.of(updated));
        when(paymentMethodRepository.save(updated)).thenReturn(updated);

        PaymentMethodDTO returnedDTO = paymentMethodService.patchPaymentMethodById(original.getId(), passedDTO);

        assertEquals(original.getId(), returnedDTO.getId());        //same id
        assertEquals(passedDTO.getName(), returnedDTO.getName());   //updated name
        assertEquals(passedDTO.getType(), returnedDTO.getType());   //updated type
        assertEquals(passedDTO.getTransactions(),returnedDTO.getTransactions());    //updated transaction
        assertEquals("/api/payment-methods/" + original.getId(), returnedDTO.getPath());

        //Assert that the payment method was updated before saving
        assertNotEquals(original.getName(), updated.getName());
        assertNotEquals(original.getType(), updated.getType());
        assertNotEquals(original.getTransactions(), updated.getTransactions());

        verify(paymentMethodRepository, times(1)).save(updated);
    }

    @Test
    void patchPaymentMethodById_UpdateOnlyName() {

        //DTO passed to patchPaymentMethodById
        PaymentMethodDTO passedDTO = new PaymentMethodDTO();
        passedDTO.setName("TestUpdate");

        //Original PaymentMethod
        PaymentMethod original = new PaymentMethod(NAME, TYPE);
        original.setId(ID);
        original.getTransactions().add(TRANSACTION);

        //Updated Payment Method
        PaymentMethod updated = new PaymentMethod(original.getName(), original.getType());
        updated.setId(original.getId());
        updated.setTransactions(original.getTransactions());

        when(paymentMethodRepository.findById(original.getId())).thenReturn(Optional.of(updated));
        when(paymentMethodRepository.save(updated)).thenReturn(updated);

        PaymentMethodDTO returnedDTO = paymentMethodService.patchPaymentMethodById(original.getId(), passedDTO);

        assertEquals(original.getId(), returnedDTO.getId());                        //same id
        assertEquals(passedDTO.getName(), returnedDTO.getName());                   //updated name
        assertEquals(original.getType(), returnedDTO.getType());                    //same type
        assertEquals(original.getTransactions(),returnedDTO.getTransactions());     //same transactions
        assertEquals("/api/payment-methods/" + original.getId(), returnedDTO.getPath());

        //Assert that only the name was updated before saving
        assertNotEquals(original.getName(), updated.getName());
        assertEquals(original.getType(), updated.getType());
        assertEquals(original.getTransactions(), updated.getTransactions());

        verify(paymentMethodRepository, times(1)).save(updated);
    }

    @Test
    void patchPaymentMethodById_UpdateOnlyType() {

        //DTO passed to patchPaymentMethodById
        PaymentMethodDTO passedDTO = new PaymentMethodDTO();
        passedDTO.setType(PaymentType.CASH);

        //Original PaymentMethod
        PaymentMethod original = new PaymentMethod(NAME, TYPE);
        original.setId(ID);
        original.getTransactions().add(TRANSACTION);

        //Updated Payment Method
        PaymentMethod updated = new PaymentMethod(original.getName(), original.getType());
        updated.setId(original.getId());
        updated.setTransactions(original.getTransactions());

        when(paymentMethodRepository.findById(original.getId())).thenReturn(Optional.of(updated));
        when(paymentMethodRepository.save(updated)).thenReturn(updated);

        PaymentMethodDTO returnedDTO = paymentMethodService.patchPaymentMethodById(original.getId(), passedDTO);

        assertEquals(original.getId(), returnedDTO.getId());                        //same id
        assertEquals(original.getName(), returnedDTO.getName());                    //same name
        assertEquals(passedDTO.getType(), returnedDTO.getType());                   //updated type
        assertEquals(original.getTransactions(),returnedDTO.getTransactions());     //same transaction
        assertEquals("/api/payment-methods/" + original.getId(), returnedDTO.getPath());

        //Assert that only the type was updated before saving
        assertEquals(original.getName(), updated.getName());
        assertNotEquals(original.getType(), updated.getType());
        assertEquals(original.getTransactions(), updated.getTransactions());

        verify(paymentMethodRepository, times(1)).save(updated);
    }

    @Test
    void patchPaymentMethodById_UpdateOnlyTransactions() {

        //DTO passed to patchPaymentMethodById
        PaymentMethodDTO passedDTO = new PaymentMethodDTO();
        passedDTO.getTransactions().add(TRANSACTION);

        //Original PaymentMethod
        PaymentMethod original = new PaymentMethod(NAME, TYPE);
        original.setId(ID);

        //Updated Payment Method
        PaymentMethod updated = new PaymentMethod(original.getName(), original.getType());
        updated.setId(original.getId());

        when(paymentMethodRepository.findById(original.getId())).thenReturn(Optional.of(updated));
        when(paymentMethodRepository.save(updated)).thenReturn(updated);

        PaymentMethodDTO returnedDTO = paymentMethodService.patchPaymentMethodById(original.getId(), passedDTO);

        assertEquals(original.getId(), returnedDTO.getId());                        //same id
        assertEquals(original.getName(), returnedDTO.getName());                    //same name
        assertEquals(original.getType(), returnedDTO.getType());                    //same type
        assertEquals(passedDTO.getTransactions(),returnedDTO.getTransactions());    //updated transactions
        assertEquals("/api/payment-methods/" + original.getId(), returnedDTO.getPath());

        //Assert that only the type was updated before saving
        assertEquals(original.getName(), updated.getName());
        assertEquals(original.getType(), updated.getType());
        assertNotEquals(original.getTransactions(), updated.getTransactions());

        verify(paymentMethodRepository, times(1)).save(updated);
    }

    @Test
    void patchPaymentMethodById_NotFound() {

        //DTO passed to patchPaymentMethodById
        PaymentMethodDTO passedDTO = new PaymentMethodDTO();
        passedDTO.getTransactions().add(TRANSACTION);

        //Original PaymentMethod
        PaymentMethod original = new PaymentMethod(NAME, TYPE);
        original.setId(ID);

        when(paymentMethodRepository.findById(original.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> paymentMethodService.patchPaymentMethodById(original.getId(), passedDTO));
    }

    @Test
    void patchPaymentMethodById_InvalidIdModification() {

        //DTO passed to patchPaymentMethodById
        PaymentMethodDTO passedDTO = new PaymentMethodDTO();
        passedDTO.setId(123);   //attempting to change id in patch

        //Original PaymentMethod
        PaymentMethod original = new PaymentMethod(NAME, TYPE);
        original.setId(ID);

        when(paymentMethodRepository.findById(original.getId())).thenReturn(Optional.of(original));

        assertThrows(InvalidIdModificationException.class,
                () -> paymentMethodService.patchPaymentMethodById(original.getId(), passedDTO));
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

}