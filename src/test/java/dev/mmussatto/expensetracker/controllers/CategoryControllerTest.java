/*
 * Created by murilo.mussatto on 01/03/2023
 */

package dev.mmussatto.expensetracker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mmussatto.expensetracker.api.model.CategoryDTO;
import dev.mmussatto.expensetracker.domain.Color;
import dev.mmussatto.expensetracker.services.CategoryService;
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
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    void getCategoryByName() throws Exception{
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(1);
        categoryDTO.setName("Test");
        categoryDTO.setColor(Color.BLUE);
        categoryDTO.setUrl("/api/categories/1");


        when(categoryService.getCategoryByName("Test")).thenReturn(categoryDTO);

        mockMvc.perform(get("/api/categories/name/Test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("Test")))
                .andExpect(jsonPath("$.color", equalTo("BLUE")))
                .andExpect(jsonPath("$.url", equalTo("/api/categories/1")));

    }

    @Test
    void getCategoryByName_NotFound() throws Exception{

        when(categoryService.getCategoryByName("Test")).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/categories/name/Test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    void getCategoryById() throws Exception{
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(1);
        categoryDTO.setName("Test");
        categoryDTO.setColor(Color.BLUE);
        categoryDTO.setUrl("/api/categories/1");


        when(categoryService.getCategoryById(1)).thenReturn(categoryDTO);

        mockMvc.perform(get("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("Test")))
                .andExpect(jsonPath("$.color", equalTo("BLUE")))
                .andExpect(jsonPath("$.url", equalTo("/api/categories/1")));
    }

    @Test
    void getCategoryById_NotFound() throws Exception{
        when(categoryService.getCategoryById(1)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/categories/1")
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
        returnDTO.setUrl("/api/categories/1");


        when(categoryService.createNewCategory(any(CategoryDTO.class))).thenReturn(returnDTO);

        mockMvc.perform(post("/api/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("Test")))
                .andExpect(jsonPath("$.color", equalTo("BLUE")))
                .andExpect(jsonPath("$.url", equalTo("/api/categories/1")));

    }

    @Test
    void updateCategoryById() throws Exception{
        CategoryDTO savedCategoryDTO = new CategoryDTO();
        savedCategoryDTO.setId(1);
        savedCategoryDTO.setName("Test");
        savedCategoryDTO.setColor(Color.BLUE);
        savedCategoryDTO.setUrl("/api/categories/1");

        CategoryDTO updatedDTO = new CategoryDTO();
        updatedDTO.setId(1);
        updatedDTO.setName("Updated Test");
        updatedDTO.setColor(Color.GREEN);
        updatedDTO.setUrl("/api/categories/1");

        when(categoryService.getCategoryById(1)).thenReturn(savedCategoryDTO);
        when(categoryService.updateCategoryById(1, updatedDTO)).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/categories/{id}", 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("Updated Test")))
                .andExpect(jsonPath("$.color", equalTo("GREEN")))
                .andExpect(jsonPath("$.url", equalTo("/api/categories/1")));
    }

    @Test
    void updateCategoryByName() throws Exception{
        CategoryDTO savedCategoryDTO = new CategoryDTO();
        savedCategoryDTO.setId(1);
        savedCategoryDTO.setName("Test");
        savedCategoryDTO.setColor(Color.BLUE);
        savedCategoryDTO.setUrl("/api/categories/1");

        CategoryDTO updatedDTO = new CategoryDTO();
        updatedDTO.setId(1);
        updatedDTO.setName("Updated Test");
        updatedDTO.setColor(Color.GREEN);
        updatedDTO.setUrl("/api/categories/1");

        when(categoryService.getCategoryByName(savedCategoryDTO.getName())).thenReturn(savedCategoryDTO);
        when(categoryService.updateCategoryByName(savedCategoryDTO.getName(), updatedDTO)).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/categories/name/{name}", savedCategoryDTO.getName())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("Updated Test")))
                .andExpect(jsonPath("$.color", equalTo("GREEN")))
                .andExpect(jsonPath("$.url", equalTo("/api/categories/1")));
    }

    @Test
    void deleteCategoryById() throws Exception{

        mockMvc.perform(delete("/api/categories/1")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(categoryService).deleteCategoryById(anyInt());
    }

    @Test
    void testDeleteCategoryById() throws Exception{

        mockMvc.perform(delete("/api/categories/name/Test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(categoryService).deleteCategoryByName(anyString());
    }
}