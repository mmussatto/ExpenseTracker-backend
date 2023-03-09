/*
 * Created by murilo.mussatto on 07/03/2023
 */

package dev.mmussatto.expensetracker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mmussatto.expensetracker.api.model.PaymentMethodDTO;
import dev.mmussatto.expensetracker.domain.PaymentType;
import dev.mmussatto.expensetracker.services.PaymentMethodService;
import dev.mmussatto.expensetracker.services.exceptions.ResourceAlreadyExistsException;
import dev.mmussatto.expensetracker.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentMethodController.class)
class PaymentMethodControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentMethodService paymentMethodService;

    @Autowired
    private ObjectMapper objectMapper;



    @Test
    void getAllPaymentMethods() throws Exception {

        PaymentMethodDTO p1 = new PaymentMethodDTO("p1", PaymentType.CREDIT_CARD);
        p1.setId(1);

        PaymentMethodDTO p2 = new PaymentMethodDTO("p2", PaymentType.CASH);
        p2.setId(2);

        when(paymentMethodService.getAllPaymentMethods()).thenReturn(Arrays.asList(p1, p2));

        mockMvc.perform(get("/api/payment-methods").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfItems", equalTo(2)))
                .andExpect(jsonPath("$.items", hasSize(2)));
    }

    @Test
    void getPaymentMethodById() throws Exception {

        PaymentMethodDTO dto = new PaymentMethodDTO("p1", PaymentType.CREDIT_CARD);
        dto.setId(1);
        dto.setPath("/api/payment-methods/" + dto.getId());

        when(paymentMethodService.getPaymentMethodById(dto.getId())).thenReturn(dto);

        mockMvc.perform(get("/api/payment-methods/{id}", dto.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(dto.getId())))
                .andExpect(jsonPath("$.name", equalTo(dto.getName())))
                .andExpect(jsonPath("$.type", equalTo(dto.getType().toString())))
                .andExpect(jsonPath("$.path", equalTo(dto.getPath())));
    }

    @Test
    void getPaymentMethodById_NotFound() throws Exception {

        Integer notFoundId = 123;

        when(paymentMethodService.getPaymentMethodById(notFoundId)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/payment-methods/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
    }

    @Test
    void getPaymentMethodByName() throws Exception {

        PaymentMethodDTO dto = new PaymentMethodDTO("p1", PaymentType.CREDIT_CARD);
        dto.setId(1);
        dto.setPath("/api/payment-methods/" + dto.getId());

        when(paymentMethodService.getPaymentMethodByName(dto.getName())).thenReturn(dto);

        mockMvc.perform(get("/api/payment-methods/name/{name}", dto.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(dto.getId())))
                .andExpect(jsonPath("$.name", equalTo(dto.getName())))
                .andExpect(jsonPath("$.type", equalTo(dto.getType().toString())))
                .andExpect(jsonPath("$.path", equalTo(dto.getPath())));
    }

    @Test
    void getPaymentMethodByName_NotFound() throws Exception {

        String notFoundName = "Unsaved Object";

        when(paymentMethodService.getPaymentMethodByName(notFoundName)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/payment-methods/name/{name}", notFoundName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
    }

    @Test
    void createNewPaymentMethod() throws Exception {

        PaymentMethodDTO passDTO = new PaymentMethodDTO("p1", PaymentType.CREDIT_CARD);

        PaymentMethodDTO returnDTO = new PaymentMethodDTO(passDTO.getName(), passDTO.getType());
        returnDTO.setId(1);
        returnDTO.setPath("/api/payment-methods/" + returnDTO.getId());

        when(paymentMethodService.createNewPaymentMethod(passDTO)).thenReturn(returnDTO);

        mockMvc.perform(post("/api/payment-methods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(returnDTO.getId())))
                .andExpect(jsonPath("$.name", equalTo(returnDTO.getName())))
                .andExpect(jsonPath("$.type", equalTo(returnDTO.getType().toString())))
                .andExpect(jsonPath("$.path", equalTo(returnDTO.getPath())));
    }

    @Test
    void createNewPaymentMethod_AlreadyExists() throws Exception {

        PaymentMethodDTO passDTO = new PaymentMethodDTO("p1", PaymentType.CREDIT_CARD);

        when(paymentMethodService.createNewPaymentMethod(passDTO)).thenThrow(ResourceAlreadyExistsException.class);

        mockMvc.perform(post("/api/payment-methods", passDTO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passDTO)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceAlreadyExistsException));
    }

    @Test
    void updatePaymentMethodById() throws Exception {

        PaymentMethodDTO passDTO = new PaymentMethodDTO("Test Update", PaymentType.CREDIT_CARD);

        PaymentMethodDTO returnDTO = new PaymentMethodDTO(passDTO.getName(), passDTO.getType());
        returnDTO.setId(1);
        returnDTO.setPath("/api/payment-methods/" + returnDTO.getId());

        when(paymentMethodService.updatePaymentMethodById(returnDTO.getId(), passDTO)).thenReturn(returnDTO);

        mockMvc.perform(put("/api/payment-methods/{id}", returnDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(returnDTO.getId())))
                .andExpect(jsonPath("$.name", equalTo(returnDTO.getName())))
                .andExpect(jsonPath("$.type", equalTo(returnDTO.getType().toString())))
                .andExpect(jsonPath("$.path", equalTo(returnDTO.getPath())));
    }

    @Test
    void updatePaymentMethodById_NotFound() throws Exception {

        Integer notFoundId = 123;

        PaymentMethodDTO passDTO = new PaymentMethodDTO("Test Update", PaymentType.CREDIT_CARD);

        when(paymentMethodService.updatePaymentMethodById(notFoundId, passDTO)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(put("/api/payment-methods/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passDTO)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
    }

    @Test
    void patchPaymentMethodById() throws Exception {

        PaymentMethodDTO passDTO = new PaymentMethodDTO("Test Update", PaymentType.CREDIT_CARD);

        PaymentMethodDTO returnDTO = new PaymentMethodDTO(passDTO.getName(), passDTO.getType());
        returnDTO.setId(1);
        returnDTO.setPath("/api/payment-methods/" + returnDTO.getId());

        when(paymentMethodService.patchPaymentMethodById(returnDTO.getId(), passDTO)).thenReturn(returnDTO);

        mockMvc.perform(patch("/api/payment-methods/{id}", returnDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(returnDTO.getId())))
                .andExpect(jsonPath("$.name", equalTo(returnDTO.getName())))
                .andExpect(jsonPath("$.type", equalTo(returnDTO.getType().toString())))
                .andExpect(jsonPath("$.path", equalTo(returnDTO.getPath())));
    }

    @Test
    void patchPaymentMethodById_NotFound() throws Exception {

        Integer notFoundId = 123;

        PaymentMethodDTO passDTO = new PaymentMethodDTO("Test Update", PaymentType.CREDIT_CARD);


        when(paymentMethodService.patchPaymentMethodById(notFoundId, passDTO)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(patch("/api/payment-methods/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passDTO)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
    }

    @Test
    void patchPaymentMethodById_AlreadyExists() throws Exception {

        Integer alreadySavedId = 122;

        PaymentMethodDTO passDTO = new PaymentMethodDTO("Test Update", PaymentType.CREDIT_CARD);

        when(paymentMethodService.patchPaymentMethodById(alreadySavedId, passDTO)).thenThrow(ResourceAlreadyExistsException.class);

        mockMvc.perform(patch("/api/payment-methods/{id}", alreadySavedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passDTO)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceAlreadyExistsException));
    }

    @Test
    void deletePaymentMethodById() throws Exception {

        Integer idToDelete = 1;

        mockMvc.perform(delete("/api/payment-methods/{id}", idToDelete)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(paymentMethodService, times(1)).deletePaymentMethodById(idToDelete);
    }

}