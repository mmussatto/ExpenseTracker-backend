/*
 * Created by murilo.mussatto on 01/03/2023
 */

package dev.mmussatto.expensetracker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mmussatto.expensetracker.api.model.CategoryDTO;
import dev.mmussatto.expensetracker.domain.Color;
import dev.mmussatto.expensetracker.domain.Transaction;
import dev.mmussatto.expensetracker.services.CategoryService;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;



    @Test
    void getAllCategories() throws Exception {

        CategoryDTO c1 = new CategoryDTO();
        c1.setId(1);
        c1.setName("C1");
        c1.setColor(Color.BLUE);

        CategoryDTO c2 = new CategoryDTO();
        c2.setId(2);
        c2.setName("C2");
        c2.setColor(Color.RED);

        when(categoryService.getAllCategories()).thenReturn(Arrays.asList(c1, c2));

        mockMvc.perform(get("/api/categories").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfItems", equalTo(2)))
                .andExpect(jsonPath("$.items", hasSize(2)));
    }

    @Test
    void getCategoryById() throws Exception
    {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(1);
        categoryDTO.setName("Test");
        categoryDTO.setColor(Color.BLUE);
        categoryDTO.setPath("/api/categories/1");

        when(categoryService.getCategoryById(categoryDTO.getId())).thenReturn(categoryDTO);

        mockMvc.perform(get("/api/categories/{id}", categoryDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(categoryDTO.getId())))
                .andExpect(jsonPath("$.name", equalTo(categoryDTO.getName())))
                .andExpect(jsonPath("$.color", equalTo(categoryDTO.getColor().toString())))
                .andExpect(jsonPath("$.path", equalTo(categoryDTO.getPath())));
    }

    @Test
    void getCategoryById_NotFound() throws Exception {

        Integer notFoundID = 123;

        when(categoryService.getCategoryById(notFoundID)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/categories/{id}", notFoundID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof  ResourceNotFoundException));
    }

    @Test
    void getCategoryByName() throws Exception {

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(1);
        categoryDTO.setName("Test");
        categoryDTO.setColor(Color.BLUE);
        categoryDTO.setPath("/api/categories/1");


        when(categoryService.getCategoryByName(categoryDTO.getName())).thenReturn(categoryDTO);

        mockMvc.perform(get("/api/categories/name/{name}", categoryDTO.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(categoryDTO.getId())))
                .andExpect(jsonPath("$.name", equalTo(categoryDTO.getName())))
                .andExpect(jsonPath("$.color", equalTo(categoryDTO.getColor().toString())))
                .andExpect(jsonPath("$.path", equalTo(categoryDTO.getPath())));

    }

    @Test
    void getCategoryByName_NotFound() throws Exception {

        String notFoundName = "Unsaved Name";

        when(categoryService.getCategoryByName(notFoundName)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/categories/name/{name}", notFoundName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
    }

    @Test
    void createNewCategory() throws Exception {

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test");
        categoryDTO.setColor(Color.BLUE);

        CategoryDTO returnDTO = new CategoryDTO();
        returnDTO.setId(1);
        returnDTO.setName(categoryDTO.getName());
        returnDTO.setColor(categoryDTO.getColor());
        returnDTO.setPath("/api/categories/1");


        when(categoryService.createNewCategory(categoryDTO)).thenReturn(returnDTO);

        mockMvc.perform(post("/api/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(returnDTO.getId())))
                .andExpect(jsonPath("$.name", equalTo(returnDTO.getName())))
                .andExpect(jsonPath("$.color", equalTo(returnDTO.getColor().toString())))
                .andExpect(jsonPath("$.path", equalTo(returnDTO.getPath())));

    }

    @Test
    void createNewCategory_AlreadyExists() throws Exception {

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(1);
        categoryDTO.setName("Test");
        categoryDTO.setColor(Color.BLUE);

        when(categoryService.createNewCategory(categoryDTO)).thenThrow(ResourceAlreadyExistsException.class);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceAlreadyExistsException));
    }

    @Test
    void updateCategoryById() throws Exception {

        CategoryDTO passDTO = new CategoryDTO();
        passDTO.setName("Updated Test");
        passDTO.setColor(Color.BLUE);

        CategoryDTO updatedDTO = new CategoryDTO();
        updatedDTO.setId(1);
        updatedDTO.setName(passDTO.getName());
        updatedDTO.setColor(passDTO.getColor());
        updatedDTO.setPath("/api/categories/" + updatedDTO.getId());

        when(categoryService.updateCategoryById(updatedDTO.getId(), passDTO)).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/categories/{id}", updatedDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(passDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(updatedDTO.getId())))
                .andExpect(jsonPath("$.name", equalTo(updatedDTO.getName())))
                .andExpect(jsonPath("$.color", equalTo(updatedDTO.getColor().toString())))
                .andExpect(jsonPath("$.path", equalTo(updatedDTO.getPath())));
    }

    @Test
    void updateCategoryById_NotFound() throws Exception {

        Integer unsaved_id = 123;

        CategoryDTO passDTO = new CategoryDTO();
        passDTO.setName("Updated Test");
        passDTO.setColor(Color.BLUE);

        when(categoryService.updateCategoryById(unsaved_id, passDTO)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(put("/api/categories/{id}", unsaved_id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passDTO)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
    }

    @Test
    void patchCategoryById() throws Exception {

        CategoryDTO passDTO = new CategoryDTO();
        passDTO.setName("Updated Test");
        passDTO.setColor(Color.BLUE);

        CategoryDTO patchedDTO = new CategoryDTO();
        patchedDTO.setId(1);
        patchedDTO.setName(passDTO.getName());
        patchedDTO.setColor(passDTO.getColor());
        patchedDTO.setPath("/api/categories/" + patchedDTO.getId());

        when(categoryService.patchCategoryById(patchedDTO.getId(), passDTO)).thenReturn(patchedDTO);

        mockMvc.perform(patch("/api/categories/{id}", patchedDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(patchedDTO.getId())))
                .andExpect(jsonPath("$.name", equalTo(patchedDTO.getName())))
                .andExpect(jsonPath("$.color", equalTo(patchedDTO.getColor().toString())))
                .andExpect(jsonPath("$.path", equalTo(patchedDTO.getPath())));
    }

    @Test
    void patchCategoryById_NotFound() throws Exception {

        Integer notFoundId = 123;

        CategoryDTO passDTO = new CategoryDTO();
        passDTO.setName("Updated Test");
        passDTO.setColor(Color.BLUE);

        when(categoryService.patchCategoryById(notFoundId, passDTO)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(patch("/api/categories/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passDTO)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));

    }

    @Test
    void patchCategoryById_AlreadyExists() throws Exception {

        Integer alreadySavedId = 122;

        CategoryDTO passDTO = new CategoryDTO();
        passDTO.setName("Updated Test");
        passDTO.setColor(Color.BLUE);

        when(categoryService.patchCategoryById(alreadySavedId, passDTO)).thenThrow(ResourceAlreadyExistsException.class);

        mockMvc.perform(patch("/api/categories/{id}", alreadySavedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passDTO)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceAlreadyExistsException));
    }

    @Test
    void deleteCategoryById() throws Exception {

        mockMvc.perform(delete("/api/categories/1")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(categoryService, times(1)).deleteCategoryById(anyInt());
    }

    @Test
    void getCategoryTransactionsById() throws Exception {

        Transaction t1 = new Transaction();
        t1.setId(1);
        t1.setAmount(53.00);
        t1.setDescription("Test Transaction 1");

        Transaction t2 = new Transaction();
        t2.setId(2);
        t2.setAmount(123.00);
        t2.setDescription("Test Transaction 2");


        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(1);
        categoryDTO.getTransactions().addAll(Arrays.asList(t1, t2));


        when(categoryService.getTransactionsById(categoryDTO.getId())).thenReturn(categoryDTO.getTransactions());

        mockMvc.perform(get("/api/categories/{id}/transactions", categoryDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getCategoryTransactionsById_NotFound() throws Exception {

        Integer notFoundID = 123;

        when(categoryService.getTransactionsById(anyInt())).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/categories/{id}/transactions", notFoundID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));


    }
}