/*
 * Created by murilo.mussatto on 27/02/2023
 */

package dev.mmussatto.expensetracker.entities.paymenetmethod;

import dev.mmussatto.expensetracker.entities.paymentmethod.PaymentMethod;
import dev.mmussatto.expensetracker.entities.paymentmethod.PaymentMethodRepository;
import dev.mmussatto.expensetracker.entities.paymentmethod.PaymentType;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class PaymentMethodRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    PaymentMethodRepository paymentMethodRepository;


    @Test
    void findByName() {
        PaymentMethod t1 = new PaymentMethod("Test 1", PaymentType.CASH);
        testEntityManager.persist(t1);

        PaymentMethod t2 = new PaymentMethod("Test 2", PaymentType.CREDIT_CARD);
        testEntityManager.persist(t2);


        PaymentMethod test = paymentMethodRepository.findByName("Test 1").get();

        assertEquals(t1.getId(), test.getId());
    }

    @Test
    void prevent_null() {
        PaymentMethod paymentMethod = new PaymentMethod();

        assertThrows(ConstraintViolationException.class, () -> testEntityManager.persist(paymentMethod));
    }
}