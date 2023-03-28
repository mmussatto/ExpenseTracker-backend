/*
 * Created by murilo.mussatto on 03/03/2023
 */

package dev.mmussatto.expensetracker.paymenetmethod;

import dev.mmussatto.expensetracker.entities.paymentmethod.PaymentMethod;
import dev.mmussatto.expensetracker.entities.paymentmethod.PaymentMethodDTO;
import dev.mmussatto.expensetracker.entities.paymentmethod.PaymentMethodMapper;
import dev.mmussatto.expensetracker.entities.paymentmethod.PaymentType;
import dev.mmussatto.expensetracker.entities.transaction.Transaction;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentMethodMapperTest {

    public static final Integer ID = 1;
    public static final String NAME = "Test";
    public static final PaymentType TYPE = PaymentType.CREDIT_CARD;
    public static final Set<Transaction> TRANSACTIONS = Stream.of(new Transaction(), new Transaction())
            .collect(Collectors.toSet());

    PaymentMethodMapper paymentMethodMapper = PaymentMethodMapper.INSTANCE;

    @Test
    void paymentMethodToPaymentMethodDTO() {
        PaymentMethod paymentMethod = new PaymentMethod(NAME, TYPE);
        paymentMethod.setId(ID);
        paymentMethod.setTransactions(TRANSACTIONS);

        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.convertToDTO(paymentMethod);

        assertEquals(paymentMethod.getId(), paymentMethodDTO.getId());
        assertEquals(paymentMethod.getName(), paymentMethodDTO.getName());
        assertEquals(paymentMethod.getType(), paymentMethodDTO.getType());
        assertEquals(paymentMethod.getTransactions(), paymentMethodDTO.getTransactions());
    }

    @Test
    void paymentMethodDTOToPaymentMethod() {
        PaymentMethodDTO paymentMethodDTO = new PaymentMethodDTO();
        paymentMethodDTO.setId(ID);
        paymentMethodDTO.setName(NAME);
        paymentMethodDTO.setType(TYPE);
        paymentMethodDTO.setTransactions(TRANSACTIONS);

        PaymentMethod paymentMethod = paymentMethodMapper.convertToEntity(paymentMethodDTO);

        assertEquals(paymentMethodDTO.getId(), paymentMethod.getId());
        assertEquals(paymentMethodDTO.getName(), paymentMethod.getName());
        assertEquals(paymentMethodDTO.getType(), paymentMethod.getType());
        assertEquals(paymentMethodDTO.getTransactions(), paymentMethod.getTransactions());


    }
}