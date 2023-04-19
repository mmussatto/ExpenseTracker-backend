/*
 * Created by murilo.mussatto on 03/03/2023
 */

package dev.mmussatto.expensetracker.entities.transaction;

import dev.mmussatto.expensetracker.entities.category.Category;
import dev.mmussatto.expensetracker.entities.helpers.Color;
import dev.mmussatto.expensetracker.entities.paymentmethod.PaymentMethod;
import dev.mmussatto.expensetracker.entities.paymentmethod.PaymentType;
import dev.mmussatto.expensetracker.entities.tag.Tag;
import dev.mmussatto.expensetracker.entities.vendor.Vendor;
import dev.mmussatto.expensetracker.entities.vendor.onlinestore.OnlineStore;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TransactionMapperTest {

    // -------------- Constants ----------------------------
    public static final Integer ID = 1;
    public static final Double AMOUNT = 500.00;
    public static final LocalDateTime TIME = LocalDateTime.now();
    public static final String DESCRIPTION = "TestDescription";
    public static final Category CATEGORY = new Category("TestCategory", Color.BLUE);
    public static final PaymentMethod PAYMENT = new PaymentMethod("TestPaymentMethod", PaymentType.CREDIT_CARD);
    public static final Set<Tag> TAGS = new HashSet<>(Arrays.asList(new Tag("TestTag1", Color.RED), new Tag("TestTag2", Color.GREEN)));
    public static final Vendor VENDOR = new OnlineStore("TestStore", "www.somewebsite.com");

    public static final Transaction testRelationships = new Transaction();  //another transaction saved with the same entities


    TransactionMapper transactionMapper = TransactionMapper.INSTANCE;


    @BeforeAll
    static void setUpEntities() {
        AtomicInteger count = new AtomicInteger(0);
        CATEGORY.setId(ID);
        PAYMENT.setId(ID);
        TAGS.forEach(tag -> tag.setId(ID+count.getAndIncrement()));
        VENDOR.setId(ID);
    }

    @BeforeAll
    static void setUpSavedTransaction() {
        testRelationships.setId(15);
        CATEGORY.getTransactions().add(testRelationships);
        PAYMENT.getTransactions().add(testRelationships);
        TAGS.forEach(tag -> tag.getTransactions().add(testRelationships));
        VENDOR.getTransactions().add(testRelationships);
    }


    // -------------- Transaction and DTO ----------------------------
    @Test
    void convertToDTO() {

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

        VENDOR.getTransactions().add(transaction);
        transaction.setVendor(VENDOR);


        TransactionDTO transactionDTO = transactionMapper.convertToDTO(transaction);


        assertEquals(transaction.getId(), transactionDTO.getId());
        assertEquals(transaction.getAmount(), transactionDTO.getAmount());
        assertEquals(transaction.getDate(), transactionDTO.getDate());
        assertEquals(transaction.getDescription(), transactionDTO.getDescription());
        assertEquals(transaction.getCategory(), transactionDTO.getCategory());
        assertEquals(transaction.getPaymentMethod(), transactionDTO.getPaymentMethod());
        assertTrue(CollectionUtils.isEqualCollection(transaction.getTags(), transactionDTO.getTags()));
        assertEquals(transaction.getVendor(), transactionDTO.getVendor());
    }

    @Test
    void convertToEntity() {

        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setId(ID);
        transactionDTO.setAmount(AMOUNT);
        transactionDTO.setDate(TIME);
        transactionDTO.setDescription(DESCRIPTION);

        transactionDTO.setCategory(CATEGORY);
        transactionDTO.setPaymentMethod(PAYMENT);
        transactionDTO.setTags(TAGS);
        transactionDTO.setVendor(VENDOR);


        Transaction transaction = transactionMapper.convertToEntity(transactionDTO);


        assertEquals(transactionDTO.getId(), transaction.getId());
        assertEquals(transactionDTO.getAmount(), transaction.getAmount());
        assertEquals(transactionDTO.getDate(), transaction.getDate());
        assertEquals(transactionDTO.getDescription(), transaction.getDescription());
        assertEquals(transactionDTO.getCategory(), transaction.getCategory());
        assertEquals(transactionDTO.getPaymentMethod(), transaction.getPaymentMethod());
        assertTrue(CollectionUtils.isEqualCollection(transactionDTO.getTags(), transaction.getTags()));
        assertEquals(transactionDTO.getVendor(), transaction.getVendor());
    }



    // -------------- Transaction Request ----------------------------
    @Test
    void convertRequestToEntity() {

        //Create Request
        RequestTransactionDTO request = new RequestTransactionDTO();
        request.setAmount(AMOUNT);
        request.setDate(TIME);
        request.setDescription(DESCRIPTION);

        request.setCategoryId(CATEGORY.getId());
        request.setPaymentMethodId(PAYMENT.getId());
        request.setTagIds(TAGS.stream().map(Tag::getId).collect(Collectors.toSet()));
        request.setVendorId(VENDOR.getId());


        Transaction transaction = transactionMapper.convertRequestToEntity(request);


        assertEquals(request.getAmount(), transaction.getAmount());
        assertEquals(request.getDate(), transaction.getDate());
        assertEquals(request.getDescription(), transaction.getDescription());
        assertEquals(CATEGORY.getId(), transaction.getCategory().getId());
        assertEquals(PAYMENT.getId(), transaction.getPaymentMethod().getId());
        assertEquals(TAGS.size(), transaction.getTags().size());
        assertEquals(VENDOR.getId(), transaction.getVendor().getId());
    }

    @Test
    void mapCategory() {
        Integer categoryId = 1;

        Category category = transactionMapper.mapCategory(categoryId);

        assertEquals(categoryId, category.getId());
        assertNull(category.getName());
        assertNull(category.getColor());
        assertTrue(category.getTransactions().isEmpty());
    }

    @Test
    void mapPaymentMethod() {
        Integer paymentMethodId = 1;

        PaymentMethod paymentMethod = transactionMapper.mapPaymentMethod(paymentMethodId);

        assertEquals(paymentMethodId, paymentMethod.getId());
        assertNull(paymentMethod.getName());
        assertNull(paymentMethod.getType());
        assertTrue(paymentMethod.getTransactions().isEmpty());
    }

    @Test
    void mapTags() {
        Integer tagId = 1;

        Tag tag = transactionMapper.mapTags(tagId);

        assertEquals(tagId, tag.getId());
        assertNull(tag.getName());
        assertNull(tag.getColor());
        assertTrue(tag.getTransactions().isEmpty());
    }

    @Test
    void mapTagSet() {
        Set<Integer> tagIds = new HashSet<>(Arrays.asList(1, 2));

        Tag t1 = new Tag(); t1.setId(1);
        Tag t2 = new Tag(); t2.setId(2);
        Set<Tag> tagSet = new HashSet<>(Arrays.asList(t1, t2));

        Set<Tag> returnSet = transactionMapper.mapTagSet(tagIds);

        assertEquals(tagSet, returnSet);
    }

    @Test
    void mapVendor() {
        Integer vendorId = 1;

        Vendor vendor = transactionMapper.mapVendor(vendorId);

        assertEquals(vendorId, vendor.getId());
        assertNull(vendor.getName());
        assertTrue(vendor.getTransactions().isEmpty());
    }
}