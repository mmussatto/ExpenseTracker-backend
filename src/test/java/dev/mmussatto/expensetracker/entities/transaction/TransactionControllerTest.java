/*
 * Created by murilo.mussatto on 21/03/2023
 */

package dev.mmussatto.expensetracker.entities.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mmussatto.expensetracker.entities.category.Category;
import dev.mmussatto.expensetracker.entities.helpers.Color;
import dev.mmussatto.expensetracker.entities.paymentmethod.PaymentMethod;
import dev.mmussatto.expensetracker.entities.paymentmethod.PaymentType;
import dev.mmussatto.expensetracker.entities.tag.Tag;
import dev.mmussatto.expensetracker.entities.vendor.Vendor;
import dev.mmussatto.expensetracker.entities.vendor.onlinestore.OnlineStore;
import dev.mmussatto.expensetracker.exceptions.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 1;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private TransactionMapper transactionMapper;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void getAllTransactions() throws Exception {
        Transaction t1 = new Transaction();
        t1.setId(1);

        Transaction t2 = new Transaction();
        t2.setId(2);

        List<Transaction> transactions = Arrays.asList(t1, t2);

        TransactionDTO dto1 = new TransactionDTO();
        dto1.setId(t1.getId());

        TransactionDTO dto2 = new TransactionDTO();
        dto2.setId(t2.getId());

        List<TransactionDTO>  transactionDTOs = Arrays.asList(dto1, dto2);

        Pageable pageable = PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE, Sort.by("date"));
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), transactions.size());

        Page<Transaction> pagedTransactions = new PageImpl<Transaction>(
                transactions.subList(start, end), pageable, transactions.size());

        when(transactionService.getPaginated(DEFAULT_PAGE, DEFAULT_SIZE)).thenReturn(pagedTransactions);
        when(transactionMapper.convertToDTO(t1)).thenReturn(dto1);
        when(transactionMapper.convertToDTO(t2)).thenReturn(dto2);

        mockMvc.perform(get("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNo", equalTo(DEFAULT_PAGE)))
                .andExpect(jsonPath("$.pageSize", equalTo(DEFAULT_SIZE)))
                .andExpect(jsonPath("$.totalElements", equalTo(transactions.size())))
                .andExpect(jsonPath("$.nextPage", equalTo("/api/transactions?page=1&size=1")))
                .andExpect(jsonPath("$.previousPage", equalTo(null)))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andDo(print());
    }

    @Test
    void getTransactionById() throws Exception {

        Transaction savedEntity = new Transaction();
        savedEntity.setId(1);

        TransactionDTO returnedDTO = new TransactionDTO();
        returnedDTO.setId(savedEntity.getId());

        when(transactionService.getTransactionById(savedEntity.getId())).thenReturn(savedEntity);
        when(transactionMapper.convertToDTO(savedEntity)).thenReturn(returnedDTO);

        mockMvc.perform(get("/api/transactions/{id}", savedEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(returnedDTO.getId())))
                .andExpect(jsonPath("$.path", equalTo("/api/transactions/"+savedEntity.getId())));
    }

    @Test
    void getTransactionById_NotFound() throws Exception {

        Integer notFoundId = 123;

        when(transactionService.getTransactionById(notFoundId)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/transactions/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceNotFoundException.class)));
    }

    @Test
    void createNewTransaction() throws Exception {

        TransactionDTO passedDTO = createTransactionDTO();

        Transaction toSaveEntity = new Transaction(passedDTO.getAmount(), passedDTO.getDate(),
                passedDTO.getDescription(), passedDTO.getCategory(), passedDTO.getPaymentMethod(),
                passedDTO.getVendor(), passedDTO.getTags());

        Transaction savedEntity = new Transaction(toSaveEntity.getAmount(), toSaveEntity.getDate(),
                toSaveEntity.getDescription(), toSaveEntity.getCategory(), toSaveEntity.getPaymentMethod(),
                toSaveEntity.getVendor(), toSaveEntity.getTags());
        savedEntity.setId(1);

        TransactionDTO returnedDTO = new TransactionDTO(savedEntity.getAmount(), savedEntity.getDate(),
                savedEntity.getDescription(), savedEntity.getCategory(), savedEntity.getPaymentMethod(),
                savedEntity.getVendor(), savedEntity.getTags());
        returnedDTO.setId(savedEntity.getId());

        when(transactionMapper.convertToEntity(any())).thenReturn(toSaveEntity);
        when(transactionService.createNewTransaction(toSaveEntity)).thenReturn(savedEntity);
        when(transactionMapper.convertToDTO(savedEntity)).thenReturn(returnedDTO);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String retString = result.getResponse().getContentAsString();
                    TransactionDTO objFromJson = objectMapper.readValue(retString, TransactionDTO.class);
                    assertEquals(returnedDTO, objFromJson);
                })
                .andDo(print());
    }

    @Test
    void createNewTransaction_IdNotNull() throws Exception {

        TransactionDTO passedDTO = createTransactionDTO();
        passedDTO.setId(1);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void updateTransactionById() throws Exception {

        TransactionDTO passedDTO = createTransactionDTO();

        Transaction toUpdateEntity = new Transaction(passedDTO.getAmount(), passedDTO.getDate(),
                passedDTO.getDescription(), passedDTO.getCategory(), passedDTO.getPaymentMethod(),
                passedDTO.getVendor(), passedDTO.getTags());

        Transaction updatedEntity = new Transaction(toUpdateEntity.getAmount(), toUpdateEntity.getDate(),
                toUpdateEntity.getDescription(), toUpdateEntity.getCategory(), toUpdateEntity.getPaymentMethod(),
                toUpdateEntity.getVendor(), toUpdateEntity.getTags());
        updatedEntity.setId(1);

        TransactionDTO returnedDTO = new TransactionDTO(updatedEntity.getAmount(), updatedEntity.getDate(),
                updatedEntity.getDescription(), updatedEntity.getCategory(), updatedEntity.getPaymentMethod(),
                updatedEntity.getVendor(), updatedEntity.getTags());
        returnedDTO.setId(updatedEntity.getId());

        when(transactionMapper.convertToEntity(passedDTO)).thenReturn(toUpdateEntity);
        when(transactionService.updateTransactionById(returnedDTO.getId(), toUpdateEntity)).thenReturn(updatedEntity);
        when(transactionMapper.convertToDTO(updatedEntity)).thenReturn(returnedDTO);

        mockMvc.perform(put("/api/transactions/{id}", returnedDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String retString = result.getResponse().getContentAsString();
                    TransactionDTO objFromJson = objectMapper.readValue(retString, TransactionDTO.class);
                    assertEquals(returnedDTO, objFromJson);
                });

    }

    @Test
    void updateTransactionById_NotFound() throws Exception {

        Integer notFoundId = 123;

        TransactionDTO passedDTO = createTransactionDTO();

        Transaction toUpdateEntity = new Transaction(passedDTO.getAmount(), passedDTO.getDate(),
                passedDTO.getDescription(), passedDTO.getCategory(), passedDTO.getPaymentMethod(),
                passedDTO.getVendor(), passedDTO.getTags());


        when(transactionMapper.convertToEntity(passedDTO)).thenReturn(toUpdateEntity);
        when(transactionService.updateTransactionById(notFoundId, toUpdateEntity)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(put("/api/transactions/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceNotFoundException.class)));

    }

    @Test
    void updateTransactionById_BodyIdNotNull() throws Exception {

        Integer notFoundId = 123;

        TransactionDTO passedDTO = createTransactionDTO();
        passedDTO.setId(1);

        mockMvc.perform(put("/api/transactions/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void updateTransactionById_MissingAmountField() throws Exception {

        TransactionDTO passedDTO = createTransactionDTO();
        passedDTO.setAmount(null);

        mockMvc.perform(put("/api/transactions/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void updateTransactionById_MissingCategoryField() throws Exception {

        TransactionDTO passedDTO = createTransactionDTO();
        passedDTO.setCategory(null);

        mockMvc.perform(put("/api/transactions/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ConstraintViolationException.class)))
                .andDo(print());
    }

    @Test
    void patchTransactionById() throws Exception {

        TransactionDTO passedDTO = createTransactionDTO();

        Transaction toPatchEntity = new Transaction(passedDTO.getAmount(), passedDTO.getDate(),
                passedDTO.getDescription(), passedDTO.getCategory(), passedDTO.getPaymentMethod(),
                passedDTO.getVendor(), passedDTO.getTags());

        Transaction patchedEntity = new Transaction(toPatchEntity.getAmount(), toPatchEntity.getDate(),
                toPatchEntity.getDescription(), toPatchEntity.getCategory(), toPatchEntity.getPaymentMethod(),
                toPatchEntity.getVendor(), toPatchEntity.getTags());
        patchedEntity.setId(1);

        TransactionDTO returnedDTO = new TransactionDTO(patchedEntity.getAmount(), patchedEntity.getDate(),
                patchedEntity.getDescription(), patchedEntity.getCategory(), patchedEntity.getPaymentMethod(),
                patchedEntity.getVendor(), patchedEntity.getTags());
        returnedDTO.setId(patchedEntity.getId());

        when(transactionMapper.convertToEntity(passedDTO)).thenReturn(toPatchEntity);
        when(transactionService.patchTransactionById(returnedDTO.getId(), toPatchEntity)).thenReturn(patchedEntity);
        when(transactionMapper.convertToDTO(patchedEntity)).thenReturn(returnedDTO);

        mockMvc.perform(patch("/api/transactions/{id}", returnedDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String retString = result.getResponse().getContentAsString();
                    TransactionDTO objFromJson = objectMapper.readValue(retString, TransactionDTO.class);
                    assertEquals(returnedDTO, objFromJson);
                });

    }

    @Test
    void patchTransactionById_IdNotFound() throws Exception {

        Integer notFoundId = 123;

        TransactionDTO passedDTO = createTransactionDTO();

        Transaction toUpdateEntity = new Transaction(passedDTO.getAmount(), passedDTO.getDate(),
                passedDTO.getDescription(), passedDTO.getCategory(), passedDTO.getPaymentMethod(),
                passedDTO.getVendor(), passedDTO.getTags());


        when(transactionMapper.convertToEntity(passedDTO)).thenReturn(toUpdateEntity);
        when(transactionService.patchTransactionById(notFoundId, toUpdateEntity)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(patch("/api/transactions/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceNotFoundException.class)));
    }

    @Test
    void patchTransactionById_BodyIdNotNull() throws Exception {

        Integer notFoundId = 123;

        TransactionDTO passedDTO = createTransactionDTO();
        passedDTO.setId(1);

        mockMvc.perform(patch("/api/transactions/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void deleteTransactionById() throws Exception {

        Integer idToDelete = 1;

        mockMvc.perform(delete("/api/transactions/{id}", idToDelete)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(transactionService, times(1)).deleteTransactionById(idToDelete);

    }

    @Test
    void deleteTransactionById_IdNotFound() throws Exception {
        Integer notFoundId = 1;

        doThrow(ResourceNotFoundException.class).when(transactionService).deleteTransactionById(notFoundId);

        mockMvc.perform(delete("/api/transactions/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceNotFoundException.class)));

        verify(transactionService, times(1)).deleteTransactionById(notFoundId);
    }



    private static Transaction createTransactionEntity() {

        Category category = new Category("Test Category", Color.BLUE);
        category.setId(1);

        PaymentMethod payment_method = new PaymentMethod("Test Payment Method", PaymentType.CASH);
        payment_method.setId(1);

        Vendor vendor_os = new OnlineStore("Test Online Store", "www.test.com");
        vendor_os.setId(1);

        Tag tag1 = new Tag("Test Tag 1", Color.BLUE);
        tag1.setId(1);
        Tag tag2 = new Tag("Test Tag 2", Color.RED);
        tag2.setId(2);

        Transaction entity = new Transaction(10.0, LocalDateTime.now().withNano(0),
                "Test Transaction Description", category ,
                payment_method, vendor_os, Stream.of(tag1, tag2).collect(Collectors.toSet()));
        entity.setId(1);

        return entity;
    }

    private static TransactionDTO createTransactionDTO() {

        Category category = new Category("Test Category", Color.BLUE);
        category.setId(1);

        PaymentMethod payment_method = new PaymentMethod("Test Payment Method", PaymentType.CASH);
        payment_method.setId(1);

        Vendor vendor_os = new OnlineStore("Test Online Store", "www.test.com");
        vendor_os.setId(1);

        Tag tag1 = new Tag("Test Tag 1", Color.BLUE);
        tag1.setId(1);
        Tag tag2 = new Tag("Test Tag 2", Color.RED);
        tag2.setId(2);

        return new TransactionDTO(10.0, LocalDateTime.now().withNano(0),
                "Test Transaction Description", category, payment_method, vendor_os,
                Stream.of(tag1, tag2).collect(Collectors.toSet()));
    }
}