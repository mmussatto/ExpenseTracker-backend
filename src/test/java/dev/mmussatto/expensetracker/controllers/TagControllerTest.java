/*
 * Created by murilo.mussatto on 14/03/2023
 */

package dev.mmussatto.expensetracker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mmussatto.expensetracker.api.model.TagDTO;
import dev.mmussatto.expensetracker.domain.Color;
import dev.mmussatto.expensetracker.domain.Transaction;
import dev.mmussatto.expensetracker.services.TagService;
import dev.mmussatto.expensetracker.services.exceptions.ResourceAlreadyExistsException;
import dev.mmussatto.expensetracker.services.exceptions.ResourceNotFoundException;
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

@WebMvcTest(TagController.class)
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService tagService;

    @Autowired
    private ObjectMapper objectMapper;



    @Test
    void getAllTags() throws Exception {

        TagDTO t1 = new TagDTO("t1", Color.BLUE);
        t1.setId(1);

        TagDTO t2 = new TagDTO("t2", Color.GREEN);
        t2.setId(2);

        when(tagService.getAllTags()).thenReturn(Arrays.asList(t1,t2));

        mockMvc.perform(get("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfItems", equalTo(2)))
                .andExpect(jsonPath("$.items", hasSize(2)));
    }

    @Test
    void getTagById() throws Exception {
        TagDTO dto = new TagDTO("t1", Color.BLUE);
        dto.setId(1);
        dto.setPath("/api/tags" + dto.getId());

        when(tagService.getTagById(dto.getId())).thenReturn(dto);

        mockMvc.perform(get("/api/tags/{id}", dto.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(dto.getId())))
                .andExpect(jsonPath("$.name", equalTo(dto.getName())))
                .andExpect(jsonPath("$.color", equalTo(dto.getColor().toString())))
                .andExpect(jsonPath("$.path", equalTo(dto.getPath())));
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
        TagDTO dto = new TagDTO("t1", Color.BLUE);
        dto.setId(1);
        dto.setPath("/api/tags" + dto.getId());

        when(tagService.getTagByName(dto.getName())).thenReturn(dto);

        mockMvc.perform(get("/api/tags/name/{name}", dto.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(dto.getId())))
                .andExpect(jsonPath("$.name", equalTo(dto.getName())))
                .andExpect(jsonPath("$.color", equalTo(dto.getColor().toString())))
                .andExpect(jsonPath("$.path", equalTo(dto.getPath())));
    }

    @Test
    void getTagByName_NotFound() throws Exception {
        String notFoundName = "asdf";

        when(tagService.getTagByName(notFoundName)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/tags/name/{name}", notFoundName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceNotFoundException.class)));
    }

    @Test
    void createNewTag() throws Exception {

        TagDTO passedDTO = new TagDTO("t1", Color.BLUE);

        TagDTO returnedDTO = new TagDTO(passedDTO.getName(), passedDTO.getColor());
        returnedDTO.setId(1);
        returnedDTO.setPath("/api/tags" + returnedDTO.getId());

        when(tagService.createNewTag(passedDTO)).thenReturn(returnedDTO);

        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(returnedDTO.getId())))
                .andExpect(jsonPath("$.name", equalTo(returnedDTO.getName())))
                .andExpect(jsonPath("$.color", equalTo(returnedDTO.getColor().toString())))
                .andExpect(jsonPath("$.path", equalTo(returnedDTO.getPath())));
    }

    @Test
    void createNewTag_IdNotNull() throws Exception {

        TagDTO passedDTO = new TagDTO("t1", Color.BLUE);
        passedDTO.setId(1);

        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void createNewTag_NameAlreadyExists() throws Exception {

        TagDTO passedDTO = new TagDTO("t1", Color.BLUE);

        when(tagService.createNewTag(passedDTO)).thenThrow(ResourceAlreadyExistsException.class);

        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceAlreadyExistsException.class)));
    }

    @Test
    void updateTagById() throws Exception {

        TagDTO passedDTO = new TagDTO("Test Update", Color.GREEN);

        TagDTO returnedDTO = new TagDTO(passedDTO.getName(), passedDTO.getColor());
        returnedDTO.setId(1);
        returnedDTO.setPath("/api/tags" + returnedDTO.getId());

        when(tagService.updateTagById(returnedDTO.getId(), passedDTO)).thenReturn(returnedDTO);

        mockMvc.perform(put("/api/tags/{id}", returnedDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(returnedDTO.getId())))
                .andExpect(jsonPath("$.name", equalTo(returnedDTO.getName())))
                .andExpect(jsonPath("$.color", equalTo(returnedDTO.getColor().toString())))
                .andExpect(jsonPath("$.path", equalTo(returnedDTO.getPath())));
    }

    @Test
    void updateTagById_notFound() throws Exception {

        TagDTO passedDTO = new TagDTO("Test Update", Color.GREEN);

        Integer notFoundId = 123;

        when(tagService.updateTagById(notFoundId, passedDTO)).thenThrow(ResourceNotFoundException.class);

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
        passedDTO.setId(1);

        mockMvc.perform(put("/api/tags/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void updateTagById_MissingNameField() throws Exception {

        TagDTO passedDTO = new TagDTO();
        passedDTO.setColor(Color.BLUE);

        mockMvc.perform(put("/api/tags/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void updateTagById_MissingColorField() throws Exception {

        TagDTO passedDTO = new TagDTO();
        passedDTO.setName("t1");

        mockMvc.perform(put("/api/tags/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ConstraintViolationException.class)));
    }

    @Test
    void patchTagById() throws Exception {
        TagDTO passedDTO = new TagDTO("Test Update", Color.GREEN);

        TagDTO returnedDTO = new TagDTO(passedDTO.getName(), passedDTO.getColor());
        returnedDTO.setId(1);
        returnedDTO.setPath("/api/tags" + returnedDTO.getId());

        when(tagService.patchTagById(returnedDTO.getId(), passedDTO)).thenReturn(returnedDTO);

        mockMvc.perform(patch("/api/tags/{id}", returnedDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(returnedDTO.getId())))
                .andExpect(jsonPath("$.name", equalTo(returnedDTO.getName())))
                .andExpect(jsonPath("$.color", equalTo(returnedDTO.getColor().toString())))
                .andExpect(jsonPath("$.path", equalTo(returnedDTO.getPath())));

        verify(tagService, times(1)).patchTagById(1, passedDTO);

    }

    @Test
    void patchTagById_IdNotFound() throws Exception {
        TagDTO passedDTO = new TagDTO("Test Update", Color.GREEN);

        Integer notFoundId = 123;


        when(tagService.patchTagById(notFoundId, passedDTO)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(patch("/api/tags/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceNotFoundException.class)));


    }

    @Test
    void patchTagById_IdNotNull() throws Exception {
        TagDTO passedDTO = new TagDTO("Test Update", Color.GREEN);
        passedDTO.setId(123);

        when(tagService.updateTagById(1, passedDTO)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(patch("/api/tags/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ConstraintViolationException.class)));


    }

    @Test
    void patchTagById_NameAlreadyExists() throws Exception {
        TagDTO passedDTO = new TagDTO("Test Update", Color.GREEN);

        when(tagService.patchTagById(1, passedDTO)).thenThrow(ResourceAlreadyExistsException.class);

        mockMvc.perform(patch("/api/tags/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceAlreadyExistsException.class)));

    }

    @Test
    void deleteTagById() throws Exception {
        Integer idToDelete = 1;

        mockMvc.perform(delete("/api/tags/{id}", idToDelete)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(tagService, times(1)).deleteTagById(idToDelete);
    }

    @Test
    void deleteTagById_IdNotFound() throws Exception {
        Integer notFoundId = 1;

        doThrow(ResourceNotFoundException.class).when(tagService).deleteTagById(notFoundId);

        mockMvc.perform(delete("/api/tags/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceNotFoundException.class)));

        verify(tagService, times(1)).deleteTagById(notFoundId);
    }

    @Test
    void getPaymentMethodTransactionsById() throws Exception {
        Transaction t1 = new Transaction();
        t1.setId(1);
        t1.setAmount(53.00);
        t1.setDescription("Test Transaction 1");

        Transaction t2 = new Transaction();
        t2.setId(2);
        t2.setAmount(123.00);
        t2.setDescription("Test Transaction 2");

        TagDTO tagDTO = new TagDTO();
        tagDTO.setId(1);
        tagDTO.getTransactions().addAll(Arrays.asList(t1, t2));

        when(tagService.getTagTransactionsById(tagDTO.getId())).thenReturn(tagDTO.getTransactions());

        mockMvc.perform(get("/api/tags/{id}/transactions", tagDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getPaymentMethodTransactionsById_NotFound() throws Exception {
        Integer notFoundId = 123;

        when(tagService.getTagTransactionsById(notFoundId)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/tags/{id}/transactions", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException(),
                        instanceOf(ResourceNotFoundException.class)));
    }
}