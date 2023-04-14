/*
 * Created by murilo.mussatto on 14/03/2023
 */

package dev.mmussatto.expensetracker.entities.tag;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mmussatto.expensetracker.entities.helpers.Color;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagController.class)
class TagControllerTest {

    // -------------- Constants ----------------------------
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 1;
    public static final Color COLOR = Color.BLUE;
    public static final String NAME = "Test name";
    public static final int ID = 1;


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService tagService;

    @MockBean
    private TagMapper tagMapper;

    @Autowired
    private ObjectMapper objectMapper;



    // -------------- READ ----------------------------
    @Test
    void getAllTags() throws Exception {

        Tag t1 = new Tag(NAME, COLOR);
        t1.setId(ID);

        Tag t2 = new Tag("t2", Color.GREEN);
        t2.setId(2);

        TagDTO dto1 = new TagDTO(t1.getName(), t1.getColor());
        dto1.setId(t1.getId());

        TagDTO dto2 = new TagDTO(t2.getName(), t2.getColor());
        dto2.setId(t2.getId());

        when(tagService.getAllTags()).thenReturn(Arrays.asList(t1,t2));
        when(tagMapper.convertToDTO(t1)).thenReturn(dto1);
        when(tagMapper.convertToDTO(t2)).thenReturn(dto2);

        mockMvc.perform(get("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getTagById() throws Exception {

        Tag savedEntity = new Tag(NAME, COLOR);
        savedEntity.setId(ID);

        TagDTO returnedDTO = new TagDTO(savedEntity.getName(), savedEntity.getColor());
        returnedDTO.setId(savedEntity.getId());
        returnedDTO.setPath("/api/tags" + returnedDTO.getId());

        when(tagService.getTagById(savedEntity.getId())).thenReturn(savedEntity);
        when(tagMapper.convertToDTO(savedEntity)).thenReturn(returnedDTO);

        mockMvc.perform(get("/api/tags/{id}", savedEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String retString = result.getResponse().getContentAsString();
                    TagDTO objFromJson = objectMapper.readValue(retString, TagDTO.class);
                    assertEquals(returnedDTO, objFromJson);
                });
    }

    @Test
    void getTagById_NotFound() throws Exception {

        Integer notFoundId = 123;

        when(tagService.getTagById(notFoundId)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/tags/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceNotFoundException.class)));
    }

    @Test
    void getTagByName() throws Exception {

        Tag savedEntity = new Tag(NAME, COLOR);
        savedEntity.setId(ID);

        TagDTO returnedDTO = new TagDTO(savedEntity.getName(), savedEntity.getColor());
        returnedDTO.setId(savedEntity.getId());
        returnedDTO.setPath("/api/tags" + returnedDTO.getId());

        when(tagService.getTagByName(savedEntity.getName())).thenReturn(savedEntity);
        when(tagMapper.convertToDTO(savedEntity)).thenReturn(returnedDTO);


        mockMvc.perform(get("/api/tags/name/{name}", returnedDTO.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String retString = result.getResponse().getContentAsString();
                    TagDTO objFromJson = objectMapper.readValue(retString, TagDTO.class);
                    assertEquals(returnedDTO, objFromJson);
                });
    }

    @Test
    void getTagByName_NotFound() throws Exception {

        when(tagService.getTagByName(NAME)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/tags/name/{name}", NAME)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceNotFoundException.class)));
    }


    // -------------- CREATE ----------------------------
    @Test
    void createNewTag() throws Exception {

        TagDTO passedDTO = new TagDTO(NAME, COLOR);

        Tag toSaveEntity = new Tag(passedDTO.getName(), passedDTO.getColor());

        Tag savedEntity = new Tag(toSaveEntity.getName(), toSaveEntity.getColor());
        savedEntity.setId(ID);

        TagDTO returnedDTO = new TagDTO(savedEntity.getName(), savedEntity.getColor());
        returnedDTO.setId(savedEntity.getId());
        returnedDTO.setPath("/api/tags" + returnedDTO.getId());

        when(tagMapper.convertToEntity(passedDTO)).thenReturn(toSaveEntity);
        when(tagService.createNewTag(toSaveEntity)).thenReturn(savedEntity);
        when(tagMapper.convertToDTO(savedEntity)).thenReturn(returnedDTO);

        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String retString = result.getResponse().getContentAsString();
                    TagDTO objFromJson = objectMapper.readValue(retString, TagDTO.class);
                    assertEquals(returnedDTO, objFromJson);
                });
    }

    @Test
    void createNewTag_IdNotNull() throws Exception {

        TagDTO passedDTO = new TagDTO(NAME, COLOR);
        passedDTO.setId(ID);

        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void createNewTag_MissingNameField() throws Exception {

        TagDTO passedDTO = new TagDTO();
        //missing name
        passedDTO.setColor(COLOR);

        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void createNewTag_MissingColorField() throws Exception {

        TagDTO passedDTO = new TagDTO();
        passedDTO.setName(NAME);
        //missing color

        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void createNewTag_NameAlreadyExists() throws Exception {

        TagDTO passedDTO = new TagDTO(NAME, COLOR);

        Tag toSaveEntity = new Tag(passedDTO.getName(), passedDTO.getColor());

        when(tagMapper.convertToEntity(passedDTO)).thenReturn(toSaveEntity);
        when(tagService.createNewTag(toSaveEntity)).thenThrow(ResourceAlreadyExistsException.class);

        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceAlreadyExistsException.class)));
    }


    // -------------- UPDATE ----------------------------
    @Test
    void updateTagById() throws Exception {

        Integer savedId = ID;

        TagDTO passedDTO = new TagDTO(NAME, COLOR);

        Tag toUpdateEntity = new Tag(passedDTO.getName(), passedDTO.getColor());

        Tag updatedEntity = new Tag(toUpdateEntity.getName(), toUpdateEntity.getColor());
        updatedEntity.setId(savedId);

        TagDTO returnedDTO = new TagDTO(updatedEntity.getName(), updatedEntity.getColor());
        returnedDTO.setId(updatedEntity.getId());
        returnedDTO.setPath("/api/tags" + returnedDTO.getId());

        when(tagMapper.convertToEntity(passedDTO)).thenReturn(toUpdateEntity);
        when(tagService.updateTagById(savedId, toUpdateEntity)).thenReturn(updatedEntity);
        when(tagMapper.convertToDTO(updatedEntity)).thenReturn(returnedDTO);


        mockMvc.perform(put("/api/tags/{id}", returnedDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String retString = result.getResponse().getContentAsString();
                    TagDTO objFromJson = objectMapper.readValue(retString, TagDTO.class);
                    assertEquals(returnedDTO, objFromJson);
                });
    }

    @Test
    void updateTagById_notFound() throws Exception {

        TagDTO passedDTO = new TagDTO("Test Update", Color.GREEN);

        Tag toUpdateEntity = new Tag(passedDTO.getName(), passedDTO.getColor());

        Integer notFoundId = 123;

        when(tagMapper.convertToEntity(passedDTO)).thenReturn(toUpdateEntity);
        when(tagService.updateTagById(notFoundId, toUpdateEntity)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(put("/api/tags/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceNotFoundException.class)));
    }

    @Test
    void updateTagById_BodyIdNotNull() throws Exception {

        TagDTO passedDTO = new TagDTO("Test Update", Color.GREEN);
        passedDTO.setId(ID);

        mockMvc.perform(put("/api/tags/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void updateTagById_MissingNameField() throws Exception {

        TagDTO passedDTO = new TagDTO();
        //missing name field
        passedDTO.setColor(COLOR);

        mockMvc.perform(put("/api/tags/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void updateTagById_MissingColorField() throws Exception {

        TagDTO passedDTO = new TagDTO();
        passedDTO.setName(NAME);
        //missing color

        mockMvc.perform(put("/api/tags/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void updateTagById_NameAlreadyExists() throws Exception {

        Integer savedId = ID;

        TagDTO passedDTO = new TagDTO(NAME, COLOR);

        Tag toUpdateEntity = new Tag(passedDTO.getName(), passedDTO.getColor());

        when(tagMapper.convertToEntity(passedDTO)).thenReturn(toUpdateEntity);
        when(tagService.updateTagById(savedId, toUpdateEntity)).thenThrow(ResourceAlreadyExistsException.class);

        mockMvc.perform(put("/api/tags/{id}", savedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceAlreadyExistsException.class)));
    }


    // -------------- PATCH ----------------------------
    @Test
    void patchTagById() throws Exception {

        Integer savedId = ID;

        TagDTO passedDTO = new TagDTO(NAME, COLOR);

        Tag toPatchEntity = new Tag(passedDTO.getName(), passedDTO.getColor());

        Tag patchedEntity = new Tag(toPatchEntity.getName(), toPatchEntity.getColor());
        patchedEntity.setId(savedId);

        TagDTO returnedDTO = new TagDTO(patchedEntity.getName(), patchedEntity.getColor());
        returnedDTO.setId(patchedEntity.getId());
        returnedDTO.setPath("/api/tags" + returnedDTO.getId());

        when(tagMapper.convertToEntity(passedDTO)).thenReturn(toPatchEntity);
        when(tagService.patchTagById(savedId, toPatchEntity)).thenReturn(patchedEntity);
        when(tagMapper.convertToDTO(patchedEntity)).thenReturn(returnedDTO);


        mockMvc.perform(patch("/api/tags/{id}", returnedDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String retString = result.getResponse().getContentAsString();
                    TagDTO objFromJson = objectMapper.readValue(retString, TagDTO.class);
                    assertEquals(returnedDTO, objFromJson);
                });
    }

    @Test
    void patchTagById_IdNotFound() throws Exception {

        TagDTO passedDTO = new TagDTO("Test Update", Color.GREEN);

        Tag toPatchEntity = new Tag(passedDTO.getName(), passedDTO.getColor());

        Integer notFoundId = 123;

        when(tagMapper.convertToEntity(passedDTO)).thenReturn(toPatchEntity);
        when(tagService.patchTagById(notFoundId, toPatchEntity)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(patch("/api/tags/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceNotFoundException.class)));


    }

    @Test
    void patchTagById_BodyIdNotNull() throws Exception {

        TagDTO passedDTO = new TagDTO("Test Update", Color.GREEN);
        passedDTO.setId(123);

        mockMvc.perform(patch("/api/tags/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void patchTagById_NameAlreadyExists() throws Exception {

        Integer savedId = ID;

        TagDTO passedDTO = new TagDTO("Test Update", Color.GREEN);

        Tag toPatchEntity = new Tag(passedDTO.getName(), passedDTO.getColor());


        when(tagMapper.convertToEntity(passedDTO)).thenReturn(toPatchEntity);
        when(tagService.patchTagById(savedId, toPatchEntity)).thenThrow(ResourceAlreadyExistsException.class);

        mockMvc.perform(patch("/api/tags/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceAlreadyExistsException.class)));

    }


    // -------------- DELETE ----------------------------
    @Test
    void deleteTagById() throws Exception {

        Integer idToDelete = ID;

        mockMvc.perform(delete("/api/tags/{id}", idToDelete)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(tagService, times(ID)).deleteTagById(idToDelete);
    }

    @Test
    void deleteTagById_IdNotFound() throws Exception {

        Integer notFoundId = ID;

        doThrow(ResourceNotFoundException.class).when(tagService).deleteTagById(notFoundId);

        mockMvc.perform(delete("/api/tags/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceNotFoundException.class)));

        verify(tagService, times(ID)).deleteTagById(notFoundId);
    }


    // -------------- TRANSACTIONS ----------------------------
    @Test
    void getPaymentMethodTransactionsById() throws Exception {

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

        Integer tagId = ID;

        Pageable pageable = PageRequest.of(1, DEFAULT_SIZE, Sort.by("date"));
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), transactions.size());

        Page<Transaction> pagedTransactions = new PageImpl<>(
                transactions.subList(start, end), pageable, transactions.size());


        when(tagService.getTransactionsByTagId(tagId, 1, DEFAULT_SIZE)).thenReturn(pagedTransactions);

        mockMvc.perform(get("/api/tags/{id}/transactions", tagId)
                        .contentType(MediaType.APPLICATION_JSON)
                .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNo", equalTo(1)))
                .andExpect(jsonPath("$.pageSize", equalTo(DEFAULT_SIZE)))
                .andExpect(jsonPath("$.totalElements", equalTo(transactions.size())))
                .andExpect(jsonPath("$.nextPage", equalTo("/api/tags/1/transactions?page=2&size=1")))
                .andExpect(jsonPath("$.previousPage", equalTo("/api/tags/1/transactions?page=0&size=1")))
                .andExpect(jsonPath("$.content", hasSize(ID)))
                .andDo(print());
    }

    @Test
    void getPaymentMethodTransactionsById_NotFound() throws Exception {
        Integer notFoundId = 123;

        when(tagService.getTransactionsByTagId(notFoundId, DEFAULT_PAGE, DEFAULT_SIZE)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/tags/{id}/transactions", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceNotFoundException.class)));
    }
}