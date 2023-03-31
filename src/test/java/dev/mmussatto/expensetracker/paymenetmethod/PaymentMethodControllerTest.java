/*
 * Created by murilo.mussatto on 07/03/2023
 */

package dev.mmussatto.expensetracker.paymenetmethod;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mmussatto.expensetracker.entities.paymentmethod.*;
import dev.mmussatto.expensetracker.entities.transaction.Transaction;
import dev.mmussatto.expensetracker.exceptions.ResourceAlreadyExistsException;
import dev.mmussatto.expensetracker.exceptions.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentMethodController.class)
class PaymentMethodControllerTest {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 1;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentMethodService paymentMethodService;

    @MockBean
    private PaymentMethodMapper paymentMethodMapper;

    @Autowired
    private ObjectMapper objectMapper;



    @Test
    void getAllPaymentMethods() throws Exception {

        PaymentMethod p1 = new PaymentMethod("p1", PaymentType.CREDIT_CARD);
        p1.setId(1);

        PaymentMethod p2 = new PaymentMethod("p2", PaymentType.CASH);
        p2.setId(2);

        PaymentMethodDTO dto1 = new PaymentMethodDTO(p1.getName(), p1.getType());
        dto1.setId(p1.getId());

        PaymentMethodDTO dto2 = new PaymentMethodDTO(p2.getName(), p2.getType());
        dto2.setId(p2.getId());

        when(paymentMethodService.getAllPaymentMethods()).thenReturn(Arrays.asList(p1, p2));
        when(paymentMethodMapper.convertToDTO(p1)).thenReturn(dto1);
        when(paymentMethodMapper.convertToDTO(p2)).thenReturn(dto2);

        mockMvc.perform(get("/api/payment-methods").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfItems", equalTo(2)))
                .andExpect(jsonPath("$.items", hasSize(2)));
    }

    @Test
    void getPaymentMethodById() throws Exception {

        PaymentMethod savedEntity = new PaymentMethod("p1", PaymentType.CREDIT_CARD);
        savedEntity.setId(1);

        PaymentMethodDTO returnedDTO = new PaymentMethodDTO(savedEntity.getName(), savedEntity.getType());
        returnedDTO.setId(savedEntity.getId());
        returnedDTO.setPath("/api/payment-methods/" + returnedDTO.getId());

        when(paymentMethodService.getPaymentMethodById(savedEntity.getId())).thenReturn(savedEntity);
        when(paymentMethodMapper.convertToDTO(savedEntity)).thenReturn(returnedDTO);

        mockMvc.perform(get("/api/payment-methods/{id}", savedEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(returnedDTO.getId())))
                .andExpect(jsonPath("$.name", equalTo(returnedDTO.getName())))
                .andExpect(jsonPath("$.type", equalTo(returnedDTO.getType().toString())))
                .andExpect(jsonPath("$.path", equalTo(returnedDTO.getPath())));
    }

    @Test
    void getPaymentMethodById_NotFound() throws Exception {

        Integer notFoundId = 123;

        when(paymentMethodService.getPaymentMethodById(notFoundId)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/payment-methods/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(ResourceNotFoundException.class)));
    }

    @Test
    void getPaymentMethodByName() throws Exception {

        PaymentMethod savedEntity = new PaymentMethod("p1", PaymentType.CREDIT_CARD);
        savedEntity.setId(1);

        PaymentMethodDTO returnedDTO = new PaymentMethodDTO(savedEntity.getName(), savedEntity.getType());
        returnedDTO.setId(savedEntity.getId());
        returnedDTO.setPath("/api/payment-methods/" + returnedDTO.getId());

        when(paymentMethodService.getPaymentMethodByName(savedEntity.getName())).thenReturn(savedEntity);
        when(paymentMethodMapper.convertToDTO(savedEntity)).thenReturn(returnedDTO);

        mockMvc.perform(get("/api/payment-methods/name/{name}", savedEntity.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(returnedDTO.getId())))
                .andExpect(jsonPath("$.name", equalTo(returnedDTO.getName())))
                .andExpect(jsonPath("$.type", equalTo(returnedDTO.getType().toString())))
                .andExpect(jsonPath("$.path", equalTo(returnedDTO.getPath())));
    }

    @Test
    void getPaymentMethodByName_NotFound() throws Exception {

        String notFoundName = "Unsaved Object";

        when(paymentMethodService.getPaymentMethodByName(notFoundName)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/payment-methods/name/{name}", notFoundName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(ResourceNotFoundException.class)));
    }

    @Test
    void createNewPaymentMethod() throws Exception {

        PaymentMethodDTO passedDTO = new PaymentMethodDTO("p1", PaymentType.CREDIT_CARD);

        PaymentMethod toSaveEntity = new PaymentMethod(passedDTO.getName(), passedDTO.getType());

        PaymentMethod savedEntity = new PaymentMethod(toSaveEntity.getName(), toSaveEntity.getType());
        savedEntity.setId(1);

        PaymentMethodDTO returnedDTO = new PaymentMethodDTO(savedEntity.getName(), savedEntity.getType());
        returnedDTO.setId(savedEntity.getId());
        returnedDTO.setPath("/api/payment-methods/" + returnedDTO.getId());

        when(paymentMethodMapper.convertToEntity(passedDTO)).thenReturn(toSaveEntity);
        when(paymentMethodService.createNewPaymentMethod(toSaveEntity)).thenReturn(savedEntity);
        when(paymentMethodMapper.convertToDTO(savedEntity)).thenReturn(returnedDTO);

        mockMvc.perform(post("/api/payment-methods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(returnedDTO.getId())))
                .andExpect(jsonPath("$.name", equalTo(returnedDTO.getName())))
                .andExpect(jsonPath("$.type", equalTo(returnedDTO.getType().toString())))
                .andExpect(jsonPath("$.path", equalTo(returnedDTO.getPath())));
    }

    @Test
    void createNewPaymentMethod_IdNotNull() throws Exception {

        PaymentMethodDTO passedDTO = new PaymentMethodDTO("p1", PaymentType.CREDIT_CARD);
        passedDTO.setId(123);

        mockMvc.perform(post("/api/payment-methods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void createNewPaymentMethod_NameAlreadyExists() throws Exception {

        PaymentMethodDTO passedDTO = new PaymentMethodDTO("p1", PaymentType.CREDIT_CARD);

        PaymentMethod toSaveEntity = new PaymentMethod(passedDTO.getName(), passedDTO.getType());

        when(paymentMethodMapper.convertToEntity(passedDTO)).thenReturn(toSaveEntity);
        when(paymentMethodService.createNewPaymentMethod(toSaveEntity)).thenThrow(ResourceAlreadyExistsException.class);

        mockMvc.perform(post("/api/payment-methods", passedDTO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(ResourceAlreadyExistsException.class)));
    }

    @Test
    void updatePaymentMethodById() throws Exception {

        Integer savedID = 1;

        PaymentMethodDTO passedDTO = new PaymentMethodDTO("Test Update", PaymentType.CREDIT_CARD);

        PaymentMethod toUpdateEntity = new PaymentMethod(passedDTO.getName(), passedDTO.getType());

        PaymentMethod updatedEntity = new PaymentMethod(toUpdateEntity.getName(), toUpdateEntity.getType());
        updatedEntity.setId(savedID);

        PaymentMethodDTO returnedDTO = new PaymentMethodDTO(updatedEntity.getName(), updatedEntity.getType());
        returnedDTO.setId(updatedEntity.getId());
        returnedDTO.setPath("/api/payment-methods/" + returnedDTO.getId());

        when(paymentMethodMapper.convertToEntity(passedDTO)).thenReturn(toUpdateEntity);
        when(paymentMethodService.updatePaymentMethodById(savedID, toUpdateEntity)).thenReturn(updatedEntity);
        when(paymentMethodMapper.convertToDTO(updatedEntity)).thenReturn(returnedDTO);


        mockMvc.perform(put("/api/payment-methods/{id}", savedID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(returnedDTO.getId())))
                .andExpect(jsonPath("$.name", equalTo(returnedDTO.getName())))
                .andExpect(jsonPath("$.type", equalTo(returnedDTO.getType().toString())))
                .andExpect(jsonPath("$.path", equalTo(returnedDTO.getPath())));
    }

    @Test
    void updatePaymentMethodById_NotFound() throws Exception {

        Integer notFoundId = 123;

        PaymentMethodDTO passedDTO = new PaymentMethodDTO("Test Update", PaymentType.CREDIT_CARD);

        PaymentMethod toUpdateEntity = new PaymentMethod(passedDTO.getName(), passedDTO.getType());

        when(paymentMethodMapper.convertToEntity(passedDTO)).thenReturn(toUpdateEntity);
        when(paymentMethodService.updatePaymentMethodById(notFoundId, toUpdateEntity)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(put("/api/payment-methods/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(ResourceNotFoundException.class)));
    }

    @Test
    void updatePaymentMethodById_BodyIdNotNull() throws Exception {

        PaymentMethodDTO passed = new PaymentMethodDTO("Test Update", PaymentType.CREDIT_CARD);
        passed.setId(1);

        mockMvc.perform(put("/api/payment-methods/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passed)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void updatePaymentMethodById_MissingNameField() throws Exception {

        PaymentMethodDTO passedDTO = new PaymentMethodDTO();
        //missing name field
        passedDTO.setType(PaymentType.CREDIT_CARD);

        mockMvc.perform(put("/api/payment-methods/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void updatePaymentMethodById_MissingTypeField() throws Exception {

        PaymentMethodDTO passedDTO = new PaymentMethodDTO();
        passedDTO.setName("Test Update");
        //missing type field

        mockMvc.perform(put("/api/payment-methods/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(ConstraintViolationException.class)));
    }


    @Test
    void patchPaymentMethodById() throws Exception {

        Integer savedID = 1;

        PaymentMethodDTO passedDTO = new PaymentMethodDTO("Test Patch", PaymentType.CREDIT_CARD);

        PaymentMethod toPatchEntity = new PaymentMethod(passedDTO.getName(), passedDTO.getType());

        PaymentMethod patchedEntity = new PaymentMethod(toPatchEntity.getName(), toPatchEntity.getType());
        patchedEntity.setId(savedID);

        PaymentMethodDTO returnedDTO = new PaymentMethodDTO(patchedEntity.getName(), patchedEntity.getType());
        returnedDTO.setId(patchedEntity.getId());
        returnedDTO.setPath("/api/payment-methods/" + returnedDTO.getId());

        when(paymentMethodMapper.convertToEntity(passedDTO)).thenReturn(toPatchEntity);
        when(paymentMethodService.patchPaymentMethodById(savedID, toPatchEntity)).thenReturn(patchedEntity);
        when(paymentMethodMapper.convertToDTO(patchedEntity)).thenReturn(returnedDTO);


        mockMvc.perform(patch("/api/payment-methods/{id}", savedID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(returnedDTO.getId())))
                .andExpect(jsonPath("$.name", equalTo(returnedDTO.getName())))
                .andExpect(jsonPath("$.type", equalTo(returnedDTO.getType().toString())))
                .andExpect(jsonPath("$.path", equalTo(returnedDTO.getPath())));
    }

    @Test
    void patchPaymentMethodById_NotFound() throws Exception {

        Integer notFoundId = 123;

        PaymentMethodDTO passedDTO = new PaymentMethodDTO("Test Patch", PaymentType.CREDIT_CARD);

        PaymentMethod toPatchEntity = new PaymentMethod(passedDTO.getName(), passedDTO.getType());

        when(paymentMethodMapper.convertToEntity(passedDTO)).thenReturn(toPatchEntity);
        when(paymentMethodService.patchPaymentMethodById(notFoundId, toPatchEntity)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(patch("/api/payment-methods/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(ResourceNotFoundException.class)));
    }

    @Test
    void patchPaymentMethodById_IdNotNull() throws Exception {

        PaymentMethodDTO passedDTO = new PaymentMethodDTO("Test Update", PaymentType.CREDIT_CARD);
        passedDTO.setId(1);


        mockMvc.perform(patch("/api/payment-methods/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void deletePaymentMethodById() throws Exception {

        Integer idToDelete = 1;

        mockMvc.perform(delete("/api/payment-methods/{id}", idToDelete)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(paymentMethodService, times(1)).deletePaymentMethodById(idToDelete);
    }

    @Test
    void deletePaymentMethodById_NotFound() throws Exception {

        doThrow(ResourceNotFoundException.class).when(paymentMethodService).deletePaymentMethodById(anyInt());


        mockMvc.perform(delete("/api/payment-methods/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(ResourceNotFoundException.class)));

        verify(paymentMethodService, times(1)).deletePaymentMethodById(anyInt());
    }

    @Test
    void getPaymentMethodTransactionsById() throws Exception{

        Transaction t1 = new Transaction();
        t1.setId(1);
        t1.setAmount(53.00);
        t1.setDescription("Test Transaction 1");

        Transaction t2 = new Transaction();
        t2.setId(2);
        t2.setAmount(123.00);
        t2.setDescription("Test Transaction 2");

        List<Transaction> transactions = Arrays.asList(t1, t2);

        Integer paymentMethodId = 1;

        Pageable pageable = PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE, Sort.by("date"));
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), transactions.size());

        Page<Transaction> pagedTransactions = new PageImpl<Transaction>(
                transactions.subList(start, end), pageable, transactions.size());

        when(paymentMethodService.getPaymentMethodTransactionsById(paymentMethodId, DEFAULT_PAGE, DEFAULT_SIZE))
                .thenReturn(pagedTransactions);

        mockMvc.perform(get("/api/payment-methods/{id}/transactions", paymentMethodId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNo", equalTo(DEFAULT_PAGE)))
                .andExpect(jsonPath("$.pageSize", equalTo(DEFAULT_SIZE)))
                .andExpect(jsonPath("$.totalElements", equalTo(transactions.size())))
                .andExpect(jsonPath("$.nextPage", equalTo("/api/payment-methods/1/transactions?page=1&size=1")))
                .andExpect(jsonPath("$.previousPage", equalTo(null)))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andDo(print());
    }

    @Test
    void getPaymentMethodTransactionsById_NotFound() throws Exception{

        Integer notFoundId = 123;

        when(paymentMethodService.getPaymentMethodTransactionsById(notFoundId, DEFAULT_PAGE, DEFAULT_SIZE))
                .thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/payment-methods/{id}/transactions", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
    }
}