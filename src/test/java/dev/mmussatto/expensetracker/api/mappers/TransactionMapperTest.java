/*
 * Created by murilo.mussatto on 03/03/2023
 */

package dev.mmussatto.expensetracker.api.mappers;

import dev.mmussatto.expensetracker.api.model.TransactionDTO;
import dev.mmussatto.expensetracker.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionMapperTest {

    public static final Integer ID = 1;
    public static final Double AMOUNT = 500.00;
    public static final Timestamp TIME = Timestamp.valueOf(LocalDateTime.now());
    public static final String DESCRIPTION = "TestDescription";
    public static final PaymentMethod PAYMENT = new PaymentMethod("TestPaymentMethod", PaymentType.CREDIT_CARD);
    public static final Category CATEGORY = new Category("TestCategory", Color.BLUE);
    public static final Set<Tag> TAGS = Stream
            .of(new Tag("TestTag1", Color.RED), new Tag("TestTag2", Color.GREEN))
            .collect(Collectors.toSet());
    public static final Store STORE = new OnlineStore("TestStore", "www.somewebsite.com");

    TransactionMapper transactionMapper = TransactionMapper.INSTANCE;

    Transaction testRelationships = new Transaction();


    @BeforeEach
    void setUp () {
        testRelationships.setId(15);
        PAYMENT.getTransactions().add(testRelationships);
        CATEGORY.getTransactions().add(testRelationships);
        TAGS.forEach(tag -> tag.getTransactions().add(testRelationships));
        STORE.getTransactions().add(testRelationships);
    }


    @Test
    void transactionToTransactionDTO() {

        Transaction transaction = new Transaction();
        transaction.setId(ID);
        transaction.setAmount(AMOUNT);
        transaction.setDate(TIME);
        transaction.setDescription(DESCRIPTION);

        PAYMENT.getTransactions().add(transaction);
        transaction.setPaymentMethod(PAYMENT);

        CATEGORY.getTransactions().add(transaction);
        transaction.setCategory(CATEGORY);

        TAGS.forEach(tag -> tag.getTransactions().add(transaction));
        transaction.setTags(TAGS);

        STORE.getTransactions().add(transaction);
        transaction.setStore(STORE);

        TransactionDTO transactionDTO = transactionMapper.convertToDTO(transaction);


        assertEquals(transaction.getId(), transactionDTO.getId());
        assertEquals(transaction.getAmount(), transactionDTO.getAmount());
        assertEquals(transaction.getDate(), transactionDTO.getDate());
        assertEquals(transaction.getDescription(), transactionDTO.getDescription());
        assertEquals(transaction.getPaymentMethod(), transactionDTO.getPaymentMethod());
        assertEquals(transaction.getCategory(), transactionDTO.getCategory());
        assertEquals(transaction.getTags(), transactionDTO.getTags());
        assertEquals(transaction.getStore(), transactionDTO.getStore());

    }


    @Test
    void transactionDTOToTransaction() {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setId(ID);
        transactionDTO.setAmount(AMOUNT);
        transactionDTO.setDate(TIME);
        transactionDTO.setDescription(DESCRIPTION);

        transactionDTO.setPaymentMethod(PAYMENT);

        transactionDTO.setCategory(CATEGORY);

        transactionDTO.setTags(TAGS);

        transactionDTO.setStore(STORE);

        Transaction transaction = transactionMapper.convertToEntity(transactionDTO);

        assertEquals(transactionDTO.getId(), transaction.getId());
        assertEquals(transactionDTO.getAmount(), transaction.getAmount());
        assertEquals(transactionDTO.getDate(), transaction.getDate());
        assertEquals(transactionDTO.getDescription(), transaction.getDescription());
        assertEquals(transactionDTO.getPaymentMethod(), transaction.getPaymentMethod());
        assertEquals(transactionDTO.getCategory(), transaction.getCategory());
        assertEquals(transactionDTO.getTags(), transaction.getTags());
        assertEquals(transactionDTO.getStore(), transaction.getStore());
    }
}