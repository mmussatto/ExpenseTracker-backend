/*
 * Created by murilo.mussatto on 15/03/2023
 */

package dev.mmussatto.expensetracker.entities.vendor;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mmussatto.expensetracker.entities.transaction.Transaction;
import dev.mmussatto.expensetracker.entities.vendor.defaultvendor.DefaultVendor;
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

@WebMvcTest(VendorController.class)
class VendorControllerTest {

    // -------------- Constants ----------------------------
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 1;
    public static final String OS_NAME = "OS Test";
    public static final String PS_NAME = "PS Test";
    public static final String URL = "www.test.com";
    public static final String ADDRESS = "Test St.";
    public static final int ID = 1;


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VendorService vendorService;

    @MockBean
    private VendorMapper vendorMapper;

    @Autowired
    private ObjectMapper objectMapper;




    // -------------- READ ----------------------------
    @Test
    void getAllVendors() throws Exception{

        OnlineStore onlineStore = new OnlineStore(OS_NAME, URL);
        onlineStore.setId(ID);

        PhysicalStore physicalStore = new PhysicalStore(PS_NAME, ADDRESS);
        physicalStore.setId(2);

        when(vendorService.getAllVendors()).thenReturn(Arrays.asList(onlineStore, physicalStore));

        mockMvc.perform(get("/api/vendors")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getVendorById_OnlineStore() throws Exception{

        OnlineStore onlineStore = new OnlineStore(OS_NAME, URL);
        onlineStore.setId(ID);

        OnlineStoreDTO returnedDTO = new OnlineStoreDTO(onlineStore.getName(), onlineStore.getUrl());
        returnedDTO.setId(onlineStore.getId());

        when(vendorService.getVendorById(onlineStore.getId())).thenReturn(onlineStore);
        when(vendorMapper.convertToDTO(any(Vendor.class))).thenReturn(returnedDTO);

        mockMvc.perform(get("/api/vendors/{id}", onlineStore.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String retString = result.getResponse().getContentAsString();
                    OnlineStoreDTO objFromJson = objectMapper.readValue(retString, OnlineStoreDTO.class);
                    returnedDTO.setPath("/api/vendors/" + returnedDTO.getId()); //path is set inside controller
                    assertEquals(returnedDTO, objFromJson);
                });
    }

    @Test
    void getVendorById_PhysicalStore() throws Exception{

        PhysicalStore physicalStore = new PhysicalStore(PS_NAME, ADDRESS);
        physicalStore.setId(2);

        PhysicalStoreDTO returnedDTO = new PhysicalStoreDTO(physicalStore.getName(), physicalStore.getAddress());
        returnedDTO.setId(physicalStore.getId());

        when(vendorService.getVendorById(physicalStore.getId())).thenReturn(physicalStore);
        when(vendorMapper.convertToDTO(any(Vendor.class))).thenReturn(returnedDTO);

        mockMvc.perform(get("/api/vendors/{id}", physicalStore.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String retString = result.getResponse().getContentAsString();
                    PhysicalStoreDTO objFromJson = objectMapper.readValue(retString, PhysicalStoreDTO.class);
                    returnedDTO.setPath("/api/vendors/" + returnedDTO.getId()); //path is set inside controller
                    assertEquals(returnedDTO, objFromJson);
                });
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
    void getVendorByName_PhysicalStore() throws Exception{

        PhysicalStore savedEntity = new PhysicalStore(PS_NAME, ADDRESS);
        savedEntity.setId(ID);

        PhysicalStoreDTO returnedDTO = new PhysicalStoreDTO(savedEntity.getName(), savedEntity.getAddress());
        returnedDTO.setId(savedEntity.getId());

        when(vendorService.getVendorByName(savedEntity.getName())).thenReturn(savedEntity);
        when(vendorMapper.convertToDTO(any(Vendor.class))).thenReturn(returnedDTO);

        mockMvc.perform(get("/api/vendors/name/{name}", savedEntity.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String retString = result.getResponse().getContentAsString();
                    PhysicalStoreDTO objFromJson = objectMapper.readValue(retString, PhysicalStoreDTO.class);
                    returnedDTO.setPath("/api/vendors/" + returnedDTO.getId()); //path is set inside controller
                    assertEquals(returnedDTO, objFromJson);
                });
    }

    @Test
    void getVendorByName_NotFound() throws Exception{

        String notFoundName = "asdf";

        when(vendorService.getVendorByName(notFoundName)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/vendors/name/{name}", notFoundName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceNotFoundException.class)));
    }


    // -------------- CREATE ----------------------------
    @Test
    void createNewVendor_PhysicalStore() throws Exception{

        PhysicalStoreDTO passedDto = new PhysicalStoreDTO(PS_NAME, ADDRESS);

        PhysicalStore passedEntity = new PhysicalStore(passedDto.getName(), passedDto.getAddress());

        PhysicalStore returnedEntity = new PhysicalStore(passedEntity.getName(), passedEntity.getAddress());
        returnedEntity.setId(ID);

        PhysicalStoreDTO returnedDTO = new PhysicalStoreDTO(passedDto.getName(), passedDto.getAddress());
        returnedDTO.setId(returnedEntity.getId());

        when(vendorMapper.convertToEntity(any(VendorDTO.class))).thenReturn(passedEntity);
        when(vendorService.createNewVendor(passedEntity)).thenReturn(returnedEntity);
        when(vendorMapper.convertToDTO(any(Vendor.class))).thenReturn(returnedDTO);

        mockMvc.perform(post("/api/vendors")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String retString = result.getResponse().getContentAsString();
                    PhysicalStoreDTO objFromJson = objectMapper.readValue(retString, PhysicalStoreDTO.class);
                    returnedDTO.setPath("/api/vendors/" + returnedDTO.getId()); //path is set inside controller
                    assertEquals(returnedDTO, objFromJson);
                });
    }

    @Test
    void createNewVendor_OnlineStore() throws Exception{

        OnlineStoreDTO passedDto = new OnlineStoreDTO(PS_NAME, URL);

        OnlineStore passedEntity = new OnlineStore(passedDto.getName(), passedDto.getUrl());

        OnlineStore returnedEntity = new OnlineStore(passedEntity.getName(), passedEntity.getUrl());
        returnedEntity.setId(ID);

        OnlineStoreDTO returnedDTO = new OnlineStoreDTO(passedDto.getName(), passedDto.getUrl());
        returnedDTO.setId(returnedEntity.getId());

        when(vendorMapper.convertToEntity(any(VendorDTO.class))).thenReturn(passedEntity);
        when(vendorService.createNewVendor(passedEntity)).thenReturn(returnedEntity);
        when(vendorMapper.convertToDTO(any(Vendor.class))).thenReturn(returnedDTO);

        mockMvc.perform(post("/api/vendors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String retString = result.getResponse().getContentAsString();
                    OnlineStoreDTO objFromJson = objectMapper.readValue(retString, OnlineStoreDTO.class);
                    returnedDTO.setPath("/api/vendors/" + returnedDTO.getId()); //path is set inside controller
                    assertEquals(returnedDTO, objFromJson);
                });
    }

    @Test
    void createNewVendor_BodyIdNotNull() throws Exception{

        OnlineStoreDTO passedDto = new OnlineStoreDTO(PS_NAME, URL);
        passedDto.setId(ID);

        mockMvc.perform(post("/api/vendors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void createNewVendor_MissingNameField() throws Exception{

        OnlineStoreDTO passedDto = new OnlineStoreDTO();
        //missing name
        passedDto.setUrl(URL);

        mockMvc.perform(post("/api/vendors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void createNewVendor_MissingUrlField() throws Exception{

        OnlineStoreDTO passedDto = new OnlineStoreDTO();
        passedDto.setName(OS_NAME);
        //missing url

        mockMvc.perform(post("/api/vendors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void createNewVendor_MissingAddressField() throws Exception{

        PhysicalStore passedDto = new PhysicalStore();
        passedDto.setName(OS_NAME);
        //missing address

        mockMvc.perform(post("/api/vendors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void createNewVendor_ResourceAlreadyExists() throws Exception{

        OnlineStoreDTO passedDto = new OnlineStoreDTO(PS_NAME, URL);

        OnlineStore passedEntity = new OnlineStore(passedDto.getName(), passedDto.getUrl());

        when(vendorMapper.convertToEntity(any(VendorDTO.class))).thenReturn(passedEntity);
        when(vendorService.createNewVendor(passedEntity)).thenThrow(ResourceAlreadyExistsException.class);

        mockMvc.perform(post("/api/vendors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceAlreadyExistsException.class)));
    }

    @Test
    void createNewVendor_RejectDefaultVendor() throws Exception{

        DefaultVendor passedDto = new DefaultVendor("Should reject this vendor");

        mockMvc.perform(post("/api/vendors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(IncorrectVendorTypeException.class)));
    }



    // -------------- UPDATE ----------------------------
    @Test
    void updateVendorById() throws Exception{

        int savedId = ID;

        OnlineStoreDTO passedDto = new OnlineStoreDTO(OS_NAME, URL);

        OnlineStore toUpdateEntity = new OnlineStore(passedDto.getName(), passedDto.getUrl());

        OnlineStore updatedEntity = new OnlineStore(toUpdateEntity.getName(), toUpdateEntity.getUrl());
        updatedEntity.setId(savedId);

        OnlineStoreDTO returnedDTO = new OnlineStoreDTO(passedDto.getName(), passedDto.getUrl());
        returnedDTO.setId(updatedEntity.getId());

        when(vendorMapper.convertToEntity(any(VendorDTO.class))).thenReturn(toUpdateEntity);
        when(vendorService.updateVendorById(savedId, toUpdateEntity)).thenReturn(updatedEntity);
        when(vendorMapper.convertToDTO(any(Vendor.class))).thenReturn(returnedDTO);

        mockMvc.perform(put("/api/vendors/{id}", savedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String retString = result.getResponse().getContentAsString();
                    OnlineStoreDTO objFromJson = objectMapper.readValue(retString, OnlineStoreDTO.class);
                    returnedDTO.setPath("/api/vendors/" + returnedDTO.getId()); //path is set inside controller
                    assertEquals(returnedDTO, objFromJson);
                });
    }

    @Test
    void updateVendorById_NotFound() throws Exception{

        int notFoundId = 123;

        OnlineStoreDTO passedDto = new OnlineStoreDTO(OS_NAME, URL);

        OnlineStore passedEntity = new OnlineStore(passedDto.getName(), passedDto.getUrl());


        when(vendorMapper.convertToEntity(any(VendorDTO.class))).thenReturn(passedEntity);
        when(vendorService.updateVendorById(notFoundId, passedEntity)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(put("/api/vendors/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceNotFoundException.class)));
    }

    @Test
    void updateVendorById_BodyIdNotNull() throws Exception{

        OnlineStoreDTO passedDto = new OnlineStoreDTO(OS_NAME, URL);
        passedDto.setId(ID);

        mockMvc.perform(put("/api/vendors/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void updateVendorById_ResourceAlreadyExists() throws Exception{

        int savedId = ID;

        OnlineStoreDTO passedDto = new OnlineStoreDTO(OS_NAME, URL);

        OnlineStore passedEntity = new OnlineStore(passedDto.getName(), passedDto.getUrl());


        when(vendorMapper.convertToEntity(any(VendorDTO.class))).thenReturn(passedEntity);
        when(vendorService.updateVendorById(savedId, passedEntity)).thenThrow(ResourceAlreadyExistsException.class);

        mockMvc.perform(put("/api/vendors/{id}", savedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceAlreadyExistsException.class)));
    }

    @Test
    void updateVendorById_MissingNameField() throws Exception{

        OnlineStoreDTO passedDto = new OnlineStoreDTO();
        //missing name
        passedDto.setUrl(URL);

        mockMvc.perform(put("/api/vendors/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void updateVendorById_MissingUrlField() throws Exception{

        OnlineStoreDTO passedDto = new OnlineStoreDTO();
        passedDto.setName(OS_NAME);
        //missing url

        mockMvc.perform(put("/api/vendors/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void updateVendorById_MissingAddressField() throws Exception{

        PhysicalStore passedDto = new PhysicalStore();
        passedDto.setName(OS_NAME);
        //missing address

        mockMvc.perform(put("/api/vendors/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void updateVendorById_IncorrectType() throws Exception{

        int savedId = ID;

        OnlineStoreDTO passedDto = new OnlineStoreDTO(OS_NAME, URL);

        OnlineStore passedEntity = new OnlineStore(passedDto.getName(), passedDto.getUrl());


        when(vendorMapper.convertToEntity(any(VendorDTO.class))).thenReturn(passedEntity);
        when(vendorService.updateVendorById(savedId, passedEntity)).thenThrow(IncorrectVendorTypeException.class);

        mockMvc.perform(put("/api/vendors/{id}", savedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(IncorrectVendorTypeException.class)));
    }

    @Test
    void updateVendorById_RejectDefaultVendor() throws Exception{

        //Default vendor should not be passed to update function because it cannot be saved to the database.
        //Also, it does not have the other children properties, like "address" or "url"
        DefaultVendor passedDto = new DefaultVendor("Update Name");

        mockMvc.perform(put("/api/vendors/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(IncorrectVendorTypeException.class)));
    }


    // -------------- PATCH ----------------------------
    @Test
    void patchVendorById() throws Exception{

        int savedId = ID;

        OnlineStoreDTO passedDto = new OnlineStoreDTO(OS_NAME, URL);

        OnlineStore toPatchEntity = new OnlineStore(passedDto.getName(), passedDto.getUrl());

        OnlineStore patchedEntity = new OnlineStore(toPatchEntity.getName(), toPatchEntity.getUrl());
        patchedEntity.setId(savedId);

        OnlineStoreDTO returnedDTO = new OnlineStoreDTO(passedDto.getName(), passedDto.getUrl());
        returnedDTO.setId(patchedEntity.getId());

        when(vendorMapper.convertToEntity(any(VendorDTO.class))).thenReturn(toPatchEntity);
        when(vendorService.patchVendorById(savedId, toPatchEntity)).thenReturn(patchedEntity);
        when(vendorMapper.convertToDTO(any(Vendor.class))).thenReturn(returnedDTO);

        mockMvc.perform(patch("/api/vendors/{id}", savedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String retString = result.getResponse().getContentAsString();
                    OnlineStoreDTO objFromJson = objectMapper.readValue(retString, OnlineStoreDTO.class);
                    returnedDTO.setPath("/api/vendors/" + returnedDTO.getId()); //path is set inside controller
                    assertEquals(returnedDTO, objFromJson);
                });
    }

    @Test
    void patchVendorById_NotFound() throws Exception{

        int notFoundId = 123;

        OnlineStoreDTO passedDto = new OnlineStoreDTO(OS_NAME, URL);

        OnlineStore passedEntity = new OnlineStore(passedDto.getName(), passedDto.getUrl());


        when(vendorMapper.convertToEntity(any(VendorDTO.class))).thenReturn(passedEntity);
        when(vendorService.patchVendorById(notFoundId, passedEntity)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(patch("/api/vendors/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceNotFoundException.class)));
    }

    @Test
    void patchVendorById_BodyIdNotNull() throws Exception{

        OnlineStoreDTO passedDto = new OnlineStoreDTO(OS_NAME, URL);
        passedDto.setId(ID);

        mockMvc.perform(patch("/api/vendors/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void patchVendorById_ResourceAlreadyExists() throws Exception{

        int savedId = ID;

        OnlineStoreDTO passedDto = new OnlineStoreDTO(OS_NAME, URL);

        OnlineStore passedEntity = new OnlineStore(passedDto.getName(), passedDto.getUrl());

        when(vendorMapper.convertToEntity(any(VendorDTO.class))).thenReturn(passedEntity);
        when(vendorService.patchVendorById(savedId, passedEntity)).thenThrow(ResourceAlreadyExistsException.class);

        mockMvc.perform(patch("/api/vendors/{id}", savedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceAlreadyExistsException.class)));

    }

    @Test
    void patchVendorById_IncorrectType() throws Exception{

        int savedId = ID;

        OnlineStoreDTO passedDto = new OnlineStoreDTO(OS_NAME, URL);

        OnlineStore passedEntity = new OnlineStore(passedDto.getName(), passedDto.getUrl());


        when(vendorMapper.convertToEntity(any(VendorDTO.class))).thenReturn(passedEntity);
        when(vendorService.patchVendorById(savedId, passedEntity)).thenThrow(IncorrectVendorTypeException.class);

        mockMvc.perform(patch("/api/vendors/{id}", savedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(IncorrectVendorTypeException.class)));
    }


    // -------------- DELETE ----------------------------
    @Test
    void deleteVendorById() throws Exception{

        Integer idToDelete = ID;

        mockMvc.perform(delete("/api/vendors/{id}", idToDelete)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(vendorService, times(ID)).deleteVendorById(idToDelete);
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

        verify(vendorService, times(ID)).deleteVendorById(notFoundId);
    }


    // -------------- TRANSACTIONS ----------------------------
    @Test
    void getTransactionsByVendorId() throws Exception{

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

        Integer vendorId = ID;

        Pageable pageable = PageRequest.of(1, DEFAULT_SIZE, Sort.by("date"));
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), transactions.size());

        Page<Transaction> pagedTransactions = new PageImpl<>(
                transactions.subList(start, end), pageable, transactions.size());

        when(vendorService.getTransactionsByVendorId(vendorId, 1, DEFAULT_SIZE)).thenReturn(pagedTransactions);

        mockMvc.perform(get("/api/vendors/{id}/transactions", vendorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNo", equalTo(1)))
                .andExpect(jsonPath("$.pageSize", equalTo(DEFAULT_SIZE)))
                .andExpect(jsonPath("$.totalElements", equalTo(transactions.size())))
                .andExpect(jsonPath("$.nextPage", equalTo("/api/vendors/1/transactions?page=2&size=1")))
                .andExpect(jsonPath("$.previousPage", equalTo("/api/vendors/1/transactions?page=0&size=1")))
                .andExpect(jsonPath("$.content", hasSize(DEFAULT_SIZE)))
                .andDo(print());
    }

    @Test
    void getTransactionsByVendorId_NotFound() throws Exception {

        Integer notFoundID = 123;

        when(vendorService.getTransactionsByVendorId(notFoundID, DEFAULT_PAGE, DEFAULT_SIZE))
                .thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/vendors/{id}/transactions", notFoundID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));


    }
}