/*
 * Created by murilo.mussatto on 12/04/2023
 */

package dev.mmussatto.expensetracker.entities.transaction;

import dev.mmussatto.expensetracker.entities.category.Category;
import dev.mmussatto.expensetracker.entities.helpers.Color;
import dev.mmussatto.expensetracker.entities.paymentmethod.PaymentMethod;
import dev.mmussatto.expensetracker.entities.paymentmethod.PaymentType;
import dev.mmussatto.expensetracker.entities.tag.Tag;
import dev.mmussatto.expensetracker.entities.vendor.Vendor;
import dev.mmussatto.expensetracker.entities.vendor.onlinestore.OnlineStore;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private TransactionRepository transactionRepository;



    @Test
    void findByDateBetween() {
        Transaction t1 = createTransactionEntity();
        t1.setDate(LocalDateTime.of(2023, 4, 2, 0, 0, 0).withNano(0));
        testEntityManager.persist(t1);

        Transaction t2 = createTransactionEntity();
        t2.setDate(LocalDateTime.of(2023, 4, 3, 0, 0, 0).withNano(0));
        testEntityManager.persist(t2);

        Transaction t3 = createTransactionEntity();
        t3.setDate(LocalDateTime.of(2023, 5, 3, 0, 0, 0).withNano(0));
        testEntityManager.persist(t3);

        Pageable pageable = PageRequest.of(0, 5, Sort.by("date"));

        LocalDateTime from = LocalDateTime.of(2023, 4, 1, 0, 0, 0).withNano(0);
        LocalDateTime to = LocalDateTime.of(2023, 4, 30, 23, 59, 59).withNano(0);



        Page<Transaction> returnPage = transactionRepository.findByDateBetween(pageable, from, to);

        assertEquals(2,returnPage.getTotalElements(), "Wrong number of entities returned");
    }

    @Test
    void preventNull() {
        Transaction transaction = new Transaction();

        assertThrows(ConstraintViolationException.class, () -> testEntityManager.persist(transaction));
    }

    // -------------- Helpers ----------------------------
    private Transaction createTransactionEntity() {

        Category category = new Category("Test Category", Color.BLUE);
        testEntityManager.persist(category);

        PaymentMethod payment_method = new PaymentMethod("Test Payment Method", PaymentType.CASH);
        testEntityManager.persist(payment_method);

        Vendor vendor_os = new OnlineStore("Test Online Store", "www.test.com");
        testEntityManager.persist(vendor_os);

        Tag tag1 = new Tag("Test Tag 1", Color.BLUE);
        testEntityManager.persist(tag1);
        Tag tag2 = new Tag("Test Tag 2", Color.RED);
        testEntityManager.persist(tag2);

        return new Transaction(10.0, LocalDateTime.now().withNano(0),
                "Test Transaction Description", category, payment_method, vendor_os,
                Stream.of(tag1, tag2).collect(Collectors.toSet()));
    }
}