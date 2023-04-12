/*
 * Created by murilo.mussatto on 12/04/2023
 */

package dev.mmussatto.expensetracker.entities.transaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private TransactionRepository transactionRepository;


    @BeforeEach
    void setUp() {
    }

    @Test
    void findByDateBetween() {
        Transaction t1 = new Transaction();
        t1.setDate(LocalDateTime.of(2023, 4, 2, 0, 0, 0).withNano(0));
        testEntityManager.persist(t1);

        Transaction t2 = new Transaction();
        t2.setDate(LocalDateTime.of(2023, 4, 3, 0, 0, 0).withNano(0));
        testEntityManager.persist(t2);

        Transaction t3 = new Transaction();
        t3.setDate(LocalDateTime.of(2023, 5, 3, 0, 0, 0).withNano(0));
        testEntityManager.persist(t3);

        Pageable pageable = PageRequest.of(0, 5, Sort.by("date"));

        LocalDateTime from = LocalDateTime.of(2023, 4, 1, 0, 0, 0).withNano(0);
        LocalDateTime to = LocalDateTime.of(2023, 4, 30, 23, 59, 59).withNano(0);



        Page<Transaction> returnPage = transactionRepository.findByDateBetween(pageable, from, to);

        assertEquals(2,returnPage.getTotalElements(), "Wrong number of entities returned");


    }
}