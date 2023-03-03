/*
 * Created by murilo.mussatto on 01/03/2023
 */

package dev.mmussatto.expensetracker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mmussatto.expensetracker.api.model.CategoryDTO;
import dev.mmussatto.expensetracker.domain.Color;
import dev.mmussatto.expensetracker.services.CategoryService;
import dev.mmussatto.expensetracker.services.exceptions.ResourceAlreadyExistsException;
import dev.mmussatto.expensetracker.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
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

    @BeforeEach
    void setUp() {
    }

    @Test
    void getAllCategories() throws Exception{
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
                .andExpect(jsonPath("$.numberOfCategories", equalTo(2)))
                .andExpect(jsonPath("$.categories", hasSize(2)));
    }

    @Test
    void getCategoryById() throws Exception{
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(1);
        categoryDTO.setName("Test");
        categoryDTO.setColor(Color.BLUE);
        categoryDTO.setPath("/api/categories/1");


        when(categoryService.getCategoryById(anyInt())).thenReturn(categoryDTO);

        mockMvc.perform(get("/api/categories/{id}", categoryDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("Test")))
                .andExpect(jsonPath("$.color", equalTo("BLUE")))
                .andExpect(jsonPath("$.path", equalTo("/api/categories/1")));
    }

    @Test
    void getCategoryById_NotFound() throws Exception{
        when(categoryService.getCategoryById(anyInt())).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/categories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCategoryByName() throws Exception{
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(1);
        categoryDTO.setName("Test");
        categoryDTO.setColor(Color.BLUE);
        categoryDTO.setPath("/api/categories/1");


        when(categoryService.getCategoryByName(anyString())).thenReturn(categoryDTO);

        mockMvc.perform(get("/api/categories/name/{name}", categoryDTO.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("Test")))
                .andExpect(jsonPath("$.color", equalTo("BLUE")))
                .andExpect(jsonPath("$.path", equalTo("/api/categories/1")));

    }

    @Test
    void getCategoryByName_NotFound() throws Exception{

        when(categoryService.getCategoryByName(anyString())).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/categories/name/{name}", "Test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    void createNewCategory() throws Exception{
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test");
        categoryDTO.setColor(Color.BLUE);

        CategoryDTO returnDTO = new CategoryDTO();
        returnDTO.setId(1);
        returnDTO.setName(categoryDTO.getName());
        returnDTO.setColor(categoryDTO.getColor());
        returnDTO.setPath("/api/categories/1");


        when(categoryService.createNewCategory(any(CategoryDTO.class))).thenReturn(returnDTO);

        mockMvc.perform(post("/api/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("Test")))
                .andExpect(jsonPath("$.color", equalTo("BLUE")))
                .andExpect(jsonPath("$.path", equalTo("/api/categories/1")));

    }

    @Test
    void createNewCategory_AlreadyExists() throws Exception {

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(1);
        categoryDTO.setName("Test");
        categoryDTO.setColor(Color.BLUE);

        when(categoryService.createNewCategory(any(CategoryDTO.class))).thenThrow(ResourceAlreadyExistsException.class);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void updateCategoryById() throws Exception{


        CategoryDTO updatedDTO = new CategoryDTO();
        updatedDTO.setId(1);
        updatedDTO.setName("Updated Test");
        updatedDTO.setColor(Color.GREEN);
        updatedDTO.setPath("/api/categories/1");

        when(categoryService.updateCategoryById(anyInt(), any(CategoryDTO.class))).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/categories/{id}", 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("Updated Test")))
                .andExpect(jsonPath("$.color", equalTo("GREEN")))
                .andExpect(jsonPath("$.path", equalTo("/api/categories/1")));
    }

    @Test
    void updateCategoryById_NotFound() throws Exception{

        CategoryDTO updatedDTO = new CategoryDTO();
        updatedDTO.setId(1);
        updatedDTO.setName("Updated Test");
        updatedDTO.setColor(Color.GREEN);
        updatedDTO.setPath("/api/categories/1");

        when(categoryService.updateCategoryById(anyInt(), any(CategoryDTO.class))).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(put("/api/categories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCategoryByName() throws Exception{

        CategoryDTO updatedDTO = new CategoryDTO();
        updatedDTO.setId(1);
        updatedDTO.setName("Updated Test");
        updatedDTO.setColor(Color.GREEN);
        updatedDTO.setPath("/api/categories/1");

        when(categoryService.updateCategoryByName(anyString(), any(CategoryDTO.class))).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/categories/name/{name}", "Test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("Updated Test")))
                .andExpect(jsonPath("$.color", equalTo("GREEN")))
                .andExpect(jsonPath("$.path", equalTo("/api/categories/1")));
    }

    @Test
    void updateCategoryByName_NotFound() throws Exception{

        CategoryDTO updatedDTO = new CategoryDTO();
        updatedDTO.setId(1);
        updatedDTO.setName("Updated Test");
        updatedDTO.setColor(Color.GREEN);
        updatedDTO.setPath("/api/categories/1");

        when(categoryService.updateCategoryByName(anyString(), any(CategoryDTO.class))).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(put("/api/categories/name/{name}", "Test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchCategoryById() throws Exception{

        CategoryDTO patchedDTO = new CategoryDTO();
        patchedDTO.setId(1);
        patchedDTO.setName("Updated Test");
        patchedDTO.setColor(Color.GREEN);
        patchedDTO.setPath("/api/categories/1");

        when(categoryService.patchCategoryById(anyInt(), any(CategoryDTO.class))).thenReturn(patchedDTO);

        mockMvc.perform(patch("/api/categories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("Updated Test")))
                .andExpect(jsonPath("$.color", equalTo("GREEN")))
                .andExpect(jsonPath("$.path", equalTo("/api/categories/1")));
    }

    @Test
    void patchCategoryById_NotFound() throws Exception{

        CategoryDTO patchedDTO = new CategoryDTO();
        patchedDTO.setId(1);
        patchedDTO.setName("Updated Test");
        patchedDTO.setColor(Color.GREEN);
        patchedDTO.setPath("/api/categories/1");

        when(categoryService.patchCategoryById(anyInt(), any(CategoryDTO.class))).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(patch("/api/categories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchedDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchCategoryById_AlreadyExists() throws Exception{

        CategoryDTO patchedDTO = new CategoryDTO();
        patchedDTO.setId(1);
        patchedDTO.setName("Updated Test");
        patchedDTO.setColor(Color.GREEN);
        patchedDTO.setPath("/api/categories/1");

        when(categoryService.patchCategoryById(anyInt(), any(CategoryDTO.class))).thenThrow(ResourceAlreadyExistsException.class);

        mockMvc.perform(patch("/api/categories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchedDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteCategoryById() throws Exception{

        mockMvc.perform(delete("/api/categories/1")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(categoryService, times(1)).deleteCategoryById(anyInt());
    }

    @Test
    void testDeleteCategoryById() throws Exception{

        mockMvc.perform(delete("/api/categories/name/Test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(categoryService, times(1)).deleteCategoryByName(anyString());
    }
}