/*
 * Created by murilo.mussatto on 07/03/2023
 */

package dev.mmussatto.expensetracker.entities.paymenetmethod;

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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentMethodController.class)
class PaymentMethodControllerTest {

    // -------------- Constants ----------------------------
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 1;
    public static final PaymentType PAYMENT_TYPE = PaymentType.CREDIT_CARD;
    public static final String NAME = "Test";
    public static final int ID = 1;


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentMethodService paymentMethodService;

    @MockBean
    private PaymentMethodMapper paymentMethodMapper;

    @Autowired
    private ObjectMapper objectMapper;



    // -------------- READ ----------------------------
    @Test
    void getAllPaymentMethods() throws Exception {

        //Create entities
        PaymentMethod p1 = new PaymentMethod(NAME, PAYMENT_TYPE);
        p1.setId(ID);

        PaymentMethod p2 = new PaymentMethod("p2", PaymentType.CASH);
        p2.setId(2);

        //Create DTOs
        PaymentMethodDTO dto1 = new PaymentMethodDTO(p1.getName(), p1.getType());
        dto1.setId(p1.getId());

        PaymentMethodDTO dto2 = new PaymentMethodDTO(p2.getName(), p2.getType());
        dto2.setId(p2.getId());


        when(paymentMethodService.getAllPaymentMethods()).thenReturn(Arrays.asList(p1, p2));
        when(paymentMethodMapper.convertToDTO(p1)).thenReturn(dto1);
        when(paymentMethodMapper.convertToDTO(p2)).thenReturn(dto2);


        mockMvc.perform(get("/api/payment-methods").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getPaymentMethodById() throws Exception {

        PaymentMethod savedEntity = new PaymentMethod(NAME, PAYMENT_TYPE);
        savedEntity.setId(ID);

        PaymentMethodDTO returnedDTO = new PaymentMethodDTO(savedEntity.getName(), savedEntity.getType());
        returnedDTO.setId(savedEntity.getId());

        when(paymentMethodService.getPaymentMethodById(savedEntity.getId())).thenReturn(savedEntity);
        when(paymentMethodMapper.convertToDTO(savedEntity)).thenReturn(returnedDTO);

        mockMvc.perform(get("/api/payment-methods/{id}", savedEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String retString = result.getResponse().getContentAsString();
                    PaymentMethodDTO objFromJson = objectMapper.readValue(retString, PaymentMethodDTO.class);
                    returnedDTO.setPath("/api/payment-methods/" + returnedDTO.getId()); //path is set inside controller
                    assertEquals(returnedDTO, objFromJson);
                });
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

        PaymentMethod savedEntity = new PaymentMethod(NAME, PAYMENT_TYPE);
        savedEntity.setId(ID);

        PaymentMethodDTO returnedDTO = new PaymentMethodDTO(savedEntity.getName(), savedEntity.getType());
        returnedDTO.setId(savedEntity.getId());

        when(paymentMethodService.getPaymentMethodByName(savedEntity.getName())).thenReturn(savedEntity);
        when(paymentMethodMapper.convertToDTO(savedEntity)).thenReturn(returnedDTO);

        mockMvc.perform(get("/api/payment-methods/name/{name}", savedEntity.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String retString = result.getResponse().getContentAsString();
                    PaymentMethodDTO objFromJson = objectMapper.readValue(retString, PaymentMethodDTO.class);
                    returnedDTO.setPath("/api/payment-methods/" + returnedDTO.getId()); //path is set inside controller
                    assertEquals(returnedDTO, objFromJson);
                });
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


    // -------------- CREATE ----------------------------
    @Test
    void createNewPaymentMethod() throws Exception {

        PaymentMethodDTO passedDTO = new PaymentMethodDTO(NAME, PAYMENT_TYPE);

        PaymentMethod toSaveEntity = new PaymentMethod(passedDTO.getName(), passedDTO.getType());

        PaymentMethod savedEntity = new PaymentMethod(toSaveEntity.getName(), toSaveEntity.getType());
        savedEntity.setId(ID);

        PaymentMethodDTO returnedDTO = new PaymentMethodDTO(savedEntity.getName(), savedEntity.getType());
        returnedDTO.setId(savedEntity.getId());

        when(paymentMethodMapper.convertToEntity(passedDTO)).thenReturn(toSaveEntity);
        when(paymentMethodService.createNewPaymentMethod(toSaveEntity)).thenReturn(savedEntity);
        when(paymentMethodMapper.convertToDTO(savedEntity)).thenReturn(returnedDTO);

        mockMvc.perform(post("/api/payment-methods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String retString = result.getResponse().getContentAsString();
                    PaymentMethodDTO objFromJson = objectMapper.readValue(retString, PaymentMethodDTO.class);
                    returnedDTO.setPath("/api/payment-methods/" + returnedDTO.getId()); //path is set inside controller
                    assertEquals(returnedDTO, objFromJson);
                });
    }

    @Test
    void createNewPaymentMethod_BodyIdNotNull() throws Exception {

        PaymentMethodDTO passedDTO = new PaymentMethodDTO(NAME, PAYMENT_TYPE);
        passedDTO.setId(ID);

        mockMvc.perform(post("/api/payment-methods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void createNewPaymentMethod_MissingNameField() throws Exception {

        PaymentMethodDTO passedDTO = new PaymentMethodDTO();
        //missing name field
        passedDTO.setType(PAYMENT_TYPE);

        mockMvc.perform(post("/api/payment-methods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void createNewPaymentMethod_MissingTypeField() throws Exception {

        PaymentMethodDTO passedDTO = new PaymentMethodDTO();
        passedDTO.setName(NAME);
        //missing type field

        mockMvc.perform(post("/api/payment-methods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void createNewPaymentMethod_ResourceAlreadyExists() throws Exception {

        PaymentMethodDTO passedDTO = new PaymentMethodDTO(NAME, PAYMENT_TYPE);

        PaymentMethod toSaveEntity = new PaymentMethod(passedDTO.getName(), passedDTO.getType());

        when(paymentMethodMapper.convertToEntity(passedDTO)).thenReturn(toSaveEntity);
        when(paymentMethodService.createNewPaymentMethod(toSaveEntity)).thenThrow(ResourceAlreadyExistsException.class);

        mockMvc.perform(post("/api/payment-methods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(ResourceAlreadyExistsException.class)));
    }


    // -------------- UPDATE ----------------------------
    @Test
    void updatePaymentMethodById() throws Exception {

        Integer savedID = ID;

        PaymentMethodDTO passedDTO = new PaymentMethodDTO("Test Update", PAYMENT_TYPE);

        PaymentMethod toUpdateEntity = new PaymentMethod(passedDTO.getName(), passedDTO.getType());

        PaymentMethod updatedEntity = new PaymentMethod(toUpdateEntity.getName(), toUpdateEntity.getType());
        updatedEntity.setId(savedID);

        PaymentMethodDTO returnedDTO = new PaymentMethodDTO(updatedEntity.getName(), updatedEntity.getType());
        returnedDTO.setId(updatedEntity.getId());

        when(paymentMethodMapper.convertToEntity(passedDTO)).thenReturn(toUpdateEntity);
        when(paymentMethodService.updatePaymentMethodById(savedID, toUpdateEntity)).thenReturn(updatedEntity);
        when(paymentMethodMapper.convertToDTO(updatedEntity)).thenReturn(returnedDTO);


        mockMvc.perform(put("/api/payment-methods/{id}", savedID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String retString = result.getResponse().getContentAsString();
                    PaymentMethodDTO objFromJson = objectMapper.readValue(retString, PaymentMethodDTO.class);
                    returnedDTO.setPath("/api/payment-methods/" + returnedDTO.getId()); //path is set inside controller
                    assertEquals(returnedDTO, objFromJson);
                });
    }

    @Test
    void updatePaymentMethodById_NotFound() throws Exception {

        Integer notFoundId = 123;

        PaymentMethodDTO passedDTO = new PaymentMethodDTO("Test Update", PAYMENT_TYPE);

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

        PaymentMethodDTO passed = new PaymentMethodDTO(NAME, PAYMENT_TYPE);
        passed.setId(ID);

        mockMvc.perform(put("/api/payment-methods/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passed)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void updatePaymentMethodById_MissingNameField() throws Exception {

        PaymentMethodDTO passedDTO = new PaymentMethodDTO();
        //missing name field
        passedDTO.setType(PAYMENT_TYPE);

        mockMvc.perform(put("/api/payment-methods/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void updatePaymentMethodById_MissingTypeField() throws Exception {

        PaymentMethodDTO passedDTO = new PaymentMethodDTO();
        passedDTO.setName(NAME);
        //missing type field

        mockMvc.perform(put("/api/payment-methods/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void updatePaymentMethodById_ResourceAlreadyExists() throws Exception {

        Integer savedId = ID;

        PaymentMethodDTO passedDTO = new PaymentMethodDTO(NAME, PAYMENT_TYPE);

        PaymentMethod toUpdateEntity = new PaymentMethod(passedDTO.getName(), passedDTO.getType());

        when(paymentMethodMapper.convertToEntity(passedDTO)).thenReturn(toUpdateEntity);
        when(paymentMethodService.updatePaymentMethodById(savedId, toUpdateEntity)).thenThrow(ResourceAlreadyExistsException.class);

        mockMvc.perform(put("/api/payment-methods/{id}", savedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(ResourceAlreadyExistsException.class)));
    }


    // -------------- PATCH ----------------------------
    @Test
    void patchPaymentMethodById() throws Exception {

        Integer savedID = ID;

        PaymentMethodDTO passedDTO = new PaymentMethodDTO(NAME, PAYMENT_TYPE);

        PaymentMethod toPatchEntity = new PaymentMethod(passedDTO.getName(), passedDTO.getType());

        PaymentMethod patchedEntity = new PaymentMethod(toPatchEntity.getName(), toPatchEntity.getType());
        patchedEntity.setId(savedID);

        PaymentMethodDTO returnedDTO = new PaymentMethodDTO(patchedEntity.getName(), patchedEntity.getType());
        returnedDTO.setId(patchedEntity.getId());

        when(paymentMethodMapper.convertToEntity(passedDTO)).thenReturn(toPatchEntity);
        when(paymentMethodService.patchPaymentMethodById(savedID, toPatchEntity)).thenReturn(patchedEntity);
        when(paymentMethodMapper.convertToDTO(patchedEntity)).thenReturn(returnedDTO);


        mockMvc.perform(patch("/api/payment-methods/{id}", savedID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String retString = result.getResponse().getContentAsString();
                    PaymentMethodDTO objFromJson = objectMapper.readValue(retString, PaymentMethodDTO.class);
                    returnedDTO.setPath("/api/payment-methods/" + returnedDTO.getId()); //path is set inside controller
                    assertEquals(returnedDTO, objFromJson);
                });
    }

    @Test
    void patchPaymentMethodById_NotFound() throws Exception {

        Integer notFoundId = 123;

        PaymentMethodDTO passedDTO = new PaymentMethodDTO(NAME, PAYMENT_TYPE);

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
    void patchPaymentMethodById_BodyIdNotNull() throws Exception {

        PaymentMethodDTO passedDTO = new PaymentMethodDTO("Test Update", PAYMENT_TYPE);
        passedDTO.setId(ID);


        mockMvc.perform(patch("/api/payment-methods/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void patchPaymentMethodById_ResourceAlreadyExists() throws Exception {

        Integer savedId = ID;

        PaymentMethodDTO passedDTO = new PaymentMethodDTO(NAME, PAYMENT_TYPE);

        PaymentMethod toUpdateEntity = new PaymentMethod(passedDTO.getName(), passedDTO.getType());

        when(paymentMethodMapper.convertToEntity(passedDTO)).thenReturn(toUpdateEntity);
        when(paymentMethodService.patchPaymentMethodById(savedId, toUpdateEntity)).thenThrow(ResourceAlreadyExistsException.class);

        mockMvc.perform(patch("/api/payment-methods/{id}", savedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(ResourceAlreadyExistsException.class)));
    }


    // -------------- DELETE ----------------------------
    @Test
    void deletePaymentMethodById() throws Exception {

        mockMvc.perform(delete("/api/payment-methods/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(paymentMethodService, times(1)).deletePaymentMethodById(ID);
    }

    @Test
    void deletePaymentMethodById_NotFound() throws Exception {

        doThrow(ResourceNotFoundException.class).when(paymentMethodService).deletePaymentMethodById(ID);


        mockMvc.perform(delete("/api/payment-methods/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(ResourceNotFoundException.class)));

        verify(paymentMethodService, times(1)).deletePaymentMethodById(anyInt());
    }


    // -------------- TRANSACTIONS ----------------------------
    @Test
    void getPaymentMethodTransactionsById() throws Exception{

        Transaction t1 = new Transaction();
        t1.setId(ID);
        t1.setAmount(53.00);
        t1.setDescription("Test Transaction 1");

        Transaction t2 = new Transaction();
        t2.setId(2);
        t2.setAmount(123.00);
        t2.setDescription("Test Transaction 2");

        Transaction t3 = new Transaction();
        t3.setId(3);
        t3.setAmount(123.00);
        t3.setDescription("Test Transaction 3");

        List<Transaction> transactions = Arrays.asList(t1, t2, t3);

        Integer paymentMethodId = ID;

        Pageable pageable = PageRequest.of(1, DEFAULT_SIZE, Sort.by("date"));
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), transactions.size());

        Page<Transaction> pagedTransactions = new PageImpl<>(
                transactions.subList(start, end), pageable, transactions.size());


        when(paymentMethodService.getPaymentMethodTransactionsById(paymentMethodId, 1, DEFAULT_SIZE)).thenReturn(pagedTransactions);


        mockMvc.perform(get("/api/payment-methods/{id}/transactions", paymentMethodId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNo", equalTo(1)))
                .andExpect(jsonPath("$.pageSize", equalTo(DEFAULT_SIZE)))
                .andExpect(jsonPath("$.totalElements", equalTo(transactions.size())))
                .andExpect(jsonPath("$.nextPage", equalTo("/api/payment-methods/1/transactions?page=2&size=1")))
                .andExpect(jsonPath("$.previousPage", equalTo("/api/payment-methods/1/transactions?page=0&size=1")))
                .andExpect(jsonPath("$.content", hasSize(DEFAULT_SIZE)))
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