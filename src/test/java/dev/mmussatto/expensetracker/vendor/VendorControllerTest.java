/*
 * Created by murilo.mussatto on 15/03/2023
 */

package dev.mmussatto.expensetracker.vendor;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mmussatto.expensetracker.entities.transaction.Transaction;
import dev.mmussatto.expensetracker.entities.vendor.VendorController;
import dev.mmussatto.expensetracker.entities.vendor.VendorService;
import dev.mmussatto.expensetracker.entities.vendor.onlinestore.OnlineStore;
import dev.mmussatto.expensetracker.entities.vendor.onlinestore.OnlineStoreDTO;
import dev.mmussatto.expensetracker.entities.vendor.physicalstore.PhysicalStore;
import dev.mmussatto.expensetracker.entities.vendor.physicalstore.PhysicalStoreDTO;
import dev.mmussatto.expensetracker.exceptions.IncorrectVendorTypeException;
import dev.mmussatto.expensetracker.exceptions.ResourceAlreadyExistsException;
import dev.mmussatto.expensetracker.exceptions.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VendorController.class)
class VendorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VendorService vendorService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void getAllVendors() throws Exception{

        OnlineStore onlineStore = new OnlineStore("OS Test", "www.test.com");
        onlineStore.setId(1);

        PhysicalStore physicalStore = new PhysicalStore("PS Test", "Test St.");
        physicalStore.setId(2);

        when(vendorService.getAllVendors()).thenReturn(Arrays.asList(onlineStore, physicalStore));

        mockMvc.perform(get("/api/vendors")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfItems", equalTo(2)))
                .andExpect(jsonPath("$.items", hasSize(2)));
    }

    @Test
    void getVendorById_OnlineStore() throws Exception{

        OnlineStore onlineStore = new OnlineStore("OS Test", "www.test.com");
        onlineStore.setId(1);

        when(vendorService.getVendorById(onlineStore.getId())).thenReturn(onlineStore);

        mockMvc.perform(get("/api/vendors/{id}", onlineStore.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type", equalTo("Online Store")))
                .andExpect(jsonPath("$.id", equalTo(onlineStore.getId())))
                .andExpect(jsonPath("$.name", equalTo(onlineStore.getName())))
                .andExpect(jsonPath("$.url", equalTo(onlineStore.getUrl())))
                .andExpect(jsonPath("$.path", equalTo("/api/vendors/" + onlineStore.getId())));
    }

    @Test
    void getVendorById_PhysicalStore() throws Exception{

        PhysicalStore physicalStore = new PhysicalStore("PS Test", "Test St.");
        physicalStore.setId(2);

        when(vendorService.getVendorById(physicalStore.getId())).thenReturn(physicalStore);

        mockMvc.perform(get("/api/vendors/{id}", physicalStore.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type", equalTo("Physical Store")))
                .andExpect(jsonPath("$.id", equalTo(physicalStore.getId())))
                .andExpect(jsonPath("$.name", equalTo(physicalStore.getName())))
                .andExpect(jsonPath("$.address", equalTo(physicalStore.getAddress())))
                .andExpect(jsonPath("$.path", equalTo("/api/vendors/" + physicalStore.getId())));
    }

    @Test
    void getVendorById_NotFound() throws Exception{

        Integer notFoundId = 123;

        when(vendorService.getVendorById(notFoundId)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/vendors/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceNotFoundException.class)));
    }

    @Test
    void testGetVendorByName() throws Exception{

        PhysicalStore physicalStore = new PhysicalStore("PS Test", "Test St.");
        physicalStore.setId(2);

        when(vendorService.getVendorByName(physicalStore.getName())).thenReturn(physicalStore);

        mockMvc.perform(get("/api/vendors/name/{name}", physicalStore.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type", equalTo("Physical Store")))
                .andExpect(jsonPath("$.id", equalTo(physicalStore.getId())))
                .andExpect(jsonPath("$.name", equalTo(physicalStore.getName())))
                .andExpect(jsonPath("$.address", equalTo(physicalStore.getAddress())))
                .andExpect(jsonPath("$.path", equalTo("/api/vendors/" + physicalStore.getId())));
    }

    @Test
    void testGetVendorByName_NotFound() throws Exception{

        String notFoundName = "asdf";

        when(vendorService.getVendorByName(notFoundName)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/vendors/name/{name}", notFoundName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceNotFoundException.class)));
    }

    @Test
    void createNewVendor_PhysicalStore() throws Exception{

        PhysicalStoreDTO passedDto = new PhysicalStoreDTO("PS Test", "Test St.");

        PhysicalStore passedEntity = new PhysicalStore(passedDto.getName(), passedDto.getAddress());

        PhysicalStore returnedEntity = new PhysicalStore(passedEntity.getName(), passedEntity.getAddress());
        returnedEntity.setId(1);

        PhysicalStoreDTO returnedDto = new PhysicalStoreDTO(passedDto.getName(), passedDto.getAddress());
        returnedDto.setId(returnedEntity.getId());
        returnedDto.setPath("/api/vendors/" + returnedDto.getId());

        when(vendorService.createNewVendor(passedEntity)).thenReturn(returnedEntity);

        mockMvc.perform(post("/api/vendors")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type", equalTo("Physical Store")))
                .andExpect(jsonPath("$.id", equalTo(returnedDto.getId())))
                .andExpect(jsonPath("$.name", equalTo(passedDto.getName())))
                .andExpect(jsonPath("$.address", equalTo(passedDto.getAddress())))
                .andExpect(jsonPath("$.path", equalTo(returnedDto.getPath())));
    }

    @Test
    void createNewVendor_OnlineStore() throws Exception{

        OnlineStoreDTO passedDto = new OnlineStoreDTO("PS Test", "www.test.com");

        OnlineStore passedEntity = new OnlineStore(passedDto.getName(), passedDto.getUrl());

        OnlineStore returnedEntity = new OnlineStore(passedEntity.getName(), passedEntity.getUrl());
        returnedEntity.setId(1);

        OnlineStoreDTO returnedDto = new OnlineStoreDTO(passedDto.getName(), passedDto.getUrl());
        returnedDto.setId(returnedEntity.getId());
        returnedDto.setPath("/api/vendors/" + returnedDto.getId());

        when(vendorService.createNewVendor(passedEntity)).thenReturn(returnedEntity);

        mockMvc.perform(post("/api/vendors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type", equalTo("Online Store")))
                .andExpect(jsonPath("$.id", equalTo(returnedDto.getId())))
                .andExpect(jsonPath("$.name", equalTo(passedDto.getName())))
                .andExpect(jsonPath("$.url", equalTo(passedDto.getUrl())))
                .andExpect(jsonPath("$.path", equalTo(returnedDto.getPath())));
    }

    @Test
    void createNewVendor_IdNotNull() throws Exception{

        OnlineStoreDTO passedDto = new OnlineStoreDTO("PS Test", "www.test.com");
        passedDto.setId(1);

        mockMvc.perform(post("/api/vendors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void createNewVendor_NameAlreadyExists() throws Exception{

        OnlineStoreDTO passedDto = new OnlineStoreDTO("PS Test", "www.test.com");

        OnlineStore passedEntity = new OnlineStore(passedDto.getName(), passedDto.getUrl());


        when(vendorService.createNewVendor(passedEntity)).thenThrow(ResourceAlreadyExistsException.class);

        mockMvc.perform(post("/api/vendors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceAlreadyExistsException.class)));
    }

    @Test
    void updateVendorById() throws Exception{

        int savedId = 1;

        OnlineStoreDTO passedDto = new OnlineStoreDTO("PS Test Update", "www.testUpdate.com");

        OnlineStore passedEntity = new OnlineStore(passedDto.getName(), passedDto.getUrl());

        OnlineStore returnedEntity = new OnlineStore(passedEntity.getName(), passedEntity.getUrl());
        returnedEntity.setId(savedId);

        OnlineStoreDTO returnedDto = new OnlineStoreDTO(passedDto.getName(), passedDto.getUrl());
        returnedDto.setId(returnedEntity.getId());
        returnedDto.setPath("/api/vendors/" + returnedDto.getId());

        when(vendorService.updateVendorById(savedId, passedEntity)).thenReturn(returnedEntity);

        mockMvc.perform(put("/api/vendors/{id}", savedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type", equalTo("Online Store")))
                .andExpect(jsonPath("$.id", equalTo(savedId)))
                .andExpect(jsonPath("$.name", equalTo(passedDto.getName())))
                .andExpect(jsonPath("$.url", equalTo(passedDto.getUrl())))
                .andExpect(jsonPath("$.path", equalTo(returnedDto.getPath())));
    }

    @Test
    void updateVendorById_NotFound() throws Exception{

        int notFoundId = 123;

        OnlineStoreDTO passedDto = new OnlineStoreDTO("PS Test Update", "www.testUpdate.com");

        OnlineStore passedEntity = new OnlineStore(passedDto.getName(), passedDto.getUrl());


        when(vendorService.updateVendorById(notFoundId, passedEntity)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(put("/api/vendors/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceNotFoundException.class)));
    }

    @Test
    void updateVendorById_IdNotNull() throws Exception{

        OnlineStoreDTO passedDto = new OnlineStoreDTO("PS Test Update", "www.testUpdate.com");
        passedDto.setId(1);

        mockMvc.perform(put("/api/vendors/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void updateVendorById_NameAlreadyExists() throws Exception{

        int savedId = 1;

        OnlineStoreDTO passedDto = new OnlineStoreDTO("PS Test Update", "www.testUpdate.com");

        OnlineStore passedEntity = new OnlineStore(passedDto.getName(), passedDto.getUrl());


        when(vendorService.updateVendorById(savedId, passedEntity)).thenThrow(ResourceAlreadyExistsException.class);

        mockMvc.perform(put("/api/vendors/{id}", savedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceAlreadyExistsException.class)));
    }

    @Test
    void updateVendorById_MissingUrlField() throws Exception{

        OnlineStoreDTO passedDto = new OnlineStoreDTO();
        passedDto.setName("OS Test");

        mockMvc.perform(put("/api/vendors/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void updateVendorById_IncorrectType() throws Exception{

        int savedId = 1;

        OnlineStoreDTO passedDto = new OnlineStoreDTO("PS Test Update", "www.testUpdate.com");

        OnlineStore passedEntity = new OnlineStore(passedDto.getName(), passedDto.getUrl());


        when(vendorService.updateVendorById(savedId, passedEntity)).thenThrow(IncorrectVendorTypeException.class);

        mockMvc.perform(put("/api/vendors/{id}", savedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(IncorrectVendorTypeException.class)));
    }

    @Test
    void patchVendorById() throws Exception{

        int savedId = 1;

        OnlineStoreDTO passedDto = new OnlineStoreDTO("PS Test Update", "www.testUpdate.com");

        OnlineStore passedEntity = new OnlineStore(passedDto.getName(), passedDto.getUrl());

        OnlineStore returnedEntity = new OnlineStore(passedEntity.getName(), passedEntity.getUrl());
        returnedEntity.setId(savedId);

        OnlineStoreDTO returnedDto = new OnlineStoreDTO(passedDto.getName(), passedDto.getUrl());
        returnedDto.setId(returnedEntity.getId());
        returnedDto.setPath("/api/vendors/" + returnedDto.getId());

        when(vendorService.patchVendorById(savedId, passedEntity)).thenReturn(returnedEntity);

        mockMvc.perform(patch("/api/vendors/{id}", savedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type", equalTo("Online Store")))
                .andExpect(jsonPath("$.id", equalTo(savedId)))
                .andExpect(jsonPath("$.name", equalTo(passedDto.getName())))
                .andExpect(jsonPath("$.url", equalTo(passedDto.getUrl())))
                .andExpect(jsonPath("$.path", equalTo(returnedDto.getPath())));
    }

    @Test
    void patchVendorById_NotFound() throws Exception{

        int notFoundId = 123;

        OnlineStoreDTO passedDto = new OnlineStoreDTO("PS Test Update", "www.testUpdate.com");

        OnlineStore passedEntity = new OnlineStore(passedDto.getName(), passedDto.getUrl());


        when(vendorService.patchVendorById(notFoundId, passedEntity)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(patch("/api/vendors/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceNotFoundException.class)));
    }

    @Test
    void patchVendorById_IdNotNull() throws Exception{

        OnlineStoreDTO passedDto = new OnlineStoreDTO("PS Test Update", "www.testUpdate.com");
        passedDto.setId(1);

        mockMvc.perform(patch("/api/vendors/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void patchVendorById_NameAlreadyExists() throws Exception{

        int savedId = 1;

        OnlineStoreDTO passedDto = new OnlineStoreDTO("PS Test Update", "www.testUpdate.com");

        OnlineStore passedEntity = new OnlineStore(passedDto.getName(), passedDto.getUrl());

        when(vendorService.patchVendorById(savedId, passedEntity)).thenThrow(ResourceAlreadyExistsException.class);

        mockMvc.perform(patch("/api/vendors/{id}", savedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceAlreadyExistsException.class)));

    }


    @Test
    void deleteVendorById() throws Exception{

        Integer idToDelete = 1;

        mockMvc.perform(delete("/api/vendors/{id}", idToDelete)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(vendorService, times(1)).deleteVendorById(idToDelete);
    }

    @Test
    void deleteVendorById_NotFound() throws Exception{

        Integer notFoundId = 123;

        doThrow(ResourceNotFoundException.class).when(vendorService).deleteVendorById(notFoundId);

        mockMvc.perform(delete("/api/vendors/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceNotFoundException.class)));

        verify(vendorService, times(1)).deleteVendorById(notFoundId);
    }

    @Test
    void getVendorTransactionsById() throws Exception{

        Transaction t1 = new Transaction();
        t1.setId(1);
        t1.setAmount(53.00);
        t1.setDescription("Test Transaction 1");

        Transaction t2 = new Transaction();
        t2.setId(2);
        t2.setAmount(123.00);
        t2.setDescription("Test Transaction 2");

        PhysicalStore physicalStore = new PhysicalStore();
        physicalStore.setId(1);
        physicalStore.getTransactions().addAll(Arrays.asList(t1, t2));

        when(vendorService.getTransactionsById(physicalStore.getId())).thenReturn(physicalStore.getTransactions());

        mockMvc.perform(get("/api/vendors/{id}/transactions", physicalStore.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfItems", equalTo(2)))
                .andExpect(jsonPath("$.items", hasSize(2)));
    }
}