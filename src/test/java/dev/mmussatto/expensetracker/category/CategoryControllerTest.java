/*
 * Created by murilo.mussatto on 01/03/2023
 */

package dev.mmussatto.expensetracker.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mmussatto.expensetracker.entities.category.*;
import dev.mmussatto.expensetracker.entities.helpers.Color;
import dev.mmussatto.expensetracker.entities.transaction.Transaction;
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

    @MockBean
    private CategoryMapper categoryMapper;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void getAllCategories() throws Exception {

        Category c1 = new Category("C1", Color.BLUE);
        c1.setId(1);

        Category c2 = new Category("C2", Color.RED);
        c2.setId(2);


        CategoryDTO dto1 = new CategoryDTO(c1.getName(), c1.getColor());
        dto1.setId(c1.getId());
        dto1.setPath("/api/categories/" + dto1.getId());

        CategoryDTO dto2 = new CategoryDTO(c2.getName(), c2.getColor());
        dto2.setId(c2.getId());
        dto2.setPath("/api/categories/" + dto2.getId());

        when(categoryService.getAllCategories()).thenReturn(Arrays.asList(c1, c2));
        when(categoryMapper.convertToDTO(c1)).thenReturn(dto1);
        when(categoryMapper.convertToDTO(c2)).thenReturn(dto2);


        mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfItems", equalTo(2)))
                .andExpect(jsonPath("$.items", hasSize(2)));
    }

    @Test
    void getCategoryById() throws Exception
    {
        Category savedEntity = new Category("Test", Color.BLUE);
        savedEntity.setId(1);

        CategoryDTO returnedDTO = new CategoryDTO(savedEntity.getName(), savedEntity.getColor());
        returnedDTO.setId(savedEntity.getId());
        returnedDTO.setPath("/api/categories/" + returnedDTO.getId() );

        when(categoryService.getCategoryById(savedEntity.getId())).thenReturn(savedEntity);
        when(categoryMapper.convertToDTO(savedEntity)).thenReturn(returnedDTO);


        mockMvc.perform(get("/api/categories/{id}", savedEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(returnedDTO.getId())))
                .andExpect(jsonPath("$.name", equalTo(returnedDTO.getName())))
                .andExpect(jsonPath("$.color", equalTo(returnedDTO.getColor().toString())))
                .andExpect(jsonPath("$.path", equalTo(returnedDTO.getPath())));
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

        Category savedEntity = new Category("Test", Color.BLUE);
        savedEntity.setId(1);

        CategoryDTO returnedDTO = new CategoryDTO(savedEntity.getName(), savedEntity.getColor());
        returnedDTO.setId(savedEntity.getId());
        returnedDTO.setPath("/api/categories/" + returnedDTO.getId() );


        when(categoryService.getCategoryByName(savedEntity.getName())).thenReturn(savedEntity);
        when(categoryMapper.convertToDTO(savedEntity)).thenReturn(returnedDTO);


        mockMvc.perform(get("/api/categories/name/{name}", returnedDTO.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(returnedDTO.getId())))
                .andExpect(jsonPath("$.name", equalTo(returnedDTO.getName())))
                .andExpect(jsonPath("$.color", equalTo(returnedDTO.getColor().toString())))
                .andExpect(jsonPath("$.path", equalTo(returnedDTO.getPath())));

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

        CategoryDTO passedDTO = new CategoryDTO("Test", Color.BLUE);

        Category toSaveEntity = new Category(passedDTO.getName(), passedDTO.getColor());

        Category savedEntity = new Category(toSaveEntity.getName(), toSaveEntity.getColor());
        savedEntity.setId(1);

        CategoryDTO returnDTO = new CategoryDTO(savedEntity.getName(), savedEntity.getColor());
        returnDTO.setId(savedEntity.getId());
        returnDTO.setPath("/api/categories/" + returnDTO.getId());


        when(categoryMapper.convertToEntity(passedDTO)).thenReturn(toSaveEntity);
        when(categoryService.createNewCategory(toSaveEntity)).thenReturn(savedEntity);
        when(categoryMapper.convertToDTO(savedEntity)).thenReturn(returnDTO);

        mockMvc.perform(post("/api/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(returnDTO.getId())))
                .andExpect(jsonPath("$.name", equalTo(returnDTO.getName())))
                .andExpect(jsonPath("$.color", equalTo(returnDTO.getColor().toString())))
                .andExpect(jsonPath("$.path", equalTo(returnDTO.getPath())));

    }

    @Test
    void createNewCategory_BodyIdNotNull() throws Exception {

        CategoryDTO passedDTO = new CategoryDTO("Test", Color.BLUE);
        passedDTO.setId(1);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
    }

    @Test
    void createNewCategory_NameAlreadyExists() throws Exception {

        CategoryDTO passedDTO = new CategoryDTO("Test", Color.BLUE);

        Category toSaveEntity = new Category(passedDTO.getName(), passedDTO.getColor());

        when(categoryMapper.convertToEntity(passedDTO)).thenReturn(toSaveEntity);
        when(categoryService.createNewCategory(toSaveEntity))
                .thenThrow(ResourceAlreadyExistsException.class);


        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceAlreadyExistsException));
    }

    @Test
    void updateCategoryById() throws Exception {

        Integer savedId = 1;

        CategoryDTO passedDTO = new CategoryDTO("Update Test", Color.BLUE);

        Category toUpdateEntity = new Category(passedDTO.getName(), passedDTO.getColor());

        Category updatedEntity = new Category(toUpdateEntity.getName(), toUpdateEntity.getColor());
        updatedEntity.setId(savedId);

        CategoryDTO returnDTO = new CategoryDTO(updatedEntity.getName(), updatedEntity.getColor());
        returnDTO.setId(updatedEntity.getId());
        returnDTO.setPath("/api/categories/" + returnDTO.getId());


        when(categoryMapper.convertToEntity(passedDTO)).thenReturn(toUpdateEntity);
        when(categoryService.updateCategoryById(savedId, toUpdateEntity)).thenReturn(updatedEntity);
        when(categoryMapper.convertToDTO(updatedEntity)).thenReturn(returnDTO);

        mockMvc.perform(put("/api/categories/{id}", savedId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(returnDTO.getId())))
                .andExpect(jsonPath("$.name", equalTo(returnDTO.getName())))
                .andExpect(jsonPath("$.color", equalTo(returnDTO.getColor().toString())))
                .andExpect(jsonPath("$.path", equalTo(returnDTO.getPath())));
    }

    @Test
    void updateCategoryById_NotFound() throws Exception {

        Integer notFoundId = 123;

        CategoryDTO passedDTO = new CategoryDTO("Update Test", Color.BLUE);

        Category toUpdateEntity = new Category(passedDTO.getName(), passedDTO.getColor());

        when(categoryMapper.convertToEntity(passedDTO)).thenReturn(toUpdateEntity);
        when(categoryService.updateCategoryById(notFoundId, toUpdateEntity)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(put("/api/categories/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
    }

    @Test
    void updateCategoryById_MissingColorField() throws Exception {

        CategoryDTO passedDTO = new CategoryDTO();
        passedDTO.setName("Updated Test");
        //missing color field

        mockMvc.perform(put("/api/categories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
    }

    @Test
    void updateCategoryById_BodyIdNotNull() throws Exception {

        CategoryDTO passedDTO = new CategoryDTO("Update Test", Color.BLUE);
        passedDTO.setId(1);


        mockMvc.perform(put("/api/categories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
    }

    @Test
    void updateCategoryById_MissingNameField() throws Exception {

        CategoryDTO passedDTO = new CategoryDTO();
        //missing name
        passedDTO.setColor(Color.BLUE);

        mockMvc.perform(put("/api/categories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
    }

    @Test
    void patchCategoryById() throws Exception {

        Integer savedId = 1;

        CategoryDTO passedDTO = new CategoryDTO("Patch Test", Color.BLUE);

        Category toPatchEntity = new Category(passedDTO.getName(), passedDTO.getColor());

        Category patchedEntity = new Category(toPatchEntity.getName(), toPatchEntity.getColor());
        patchedEntity.setId(savedId);

        CategoryDTO returnDTO = new CategoryDTO(patchedEntity.getName(), patchedEntity.getColor());
        returnDTO.setId(patchedEntity.getId());
        returnDTO.setPath("/api/categories/" + returnDTO.getId());


        when(categoryMapper.convertToEntity(passedDTO)).thenReturn(toPatchEntity);
        when(categoryService.patchCategoryById(savedId, toPatchEntity)).thenReturn(patchedEntity);
        when(categoryMapper.convertToDTO(patchedEntity)).thenReturn(returnDTO);


        mockMvc.perform(patch("/api/categories/{id}", savedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(returnDTO.getId())))
                .andExpect(jsonPath("$.name", equalTo(returnDTO.getName())))
                .andExpect(jsonPath("$.color", equalTo(returnDTO.getColor().toString())))
                .andExpect(jsonPath("$.path", equalTo(returnDTO.getPath())));
    }

    @Test
    void patchCategoryById_NotFound() throws Exception {

        Integer notFoundId = 123;

        CategoryDTO passedDTO = new CategoryDTO("Patch Test", Color.BLUE);

        Category toPatchEntity = new Category(passedDTO.getName(), passedDTO.getColor());

        when(categoryMapper.convertToEntity(passedDTO)).thenReturn(toPatchEntity);
        when(categoryService.patchCategoryById(notFoundId, toPatchEntity)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(patch("/api/categories/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));

    }

    @Test
    void patchCategoryById_BodyIdNotNull() throws Exception {

        CategoryDTO passedDTO = new CategoryDTO("Patch Test", Color.BLUE);
        passedDTO.setId(1);

        mockMvc.perform(patch("/api/categories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
    }

    @Test
    void deleteCategoryById() throws Exception {

        mockMvc.perform(delete("/api/categories/{id}", 1)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteCategoryById(anyInt());
    }

    @Test
    void deleteCategoryById_NotFound() throws Exception {

        doThrow(ResourceNotFoundException.class).when(categoryService).deleteCategoryById(anyInt());

        mockMvc.perform(delete("/api/categories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));

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
                .andExpect(jsonPath("$.numberOfItems", equalTo(2)))
                .andExpect(jsonPath("$.items", hasSize(2)));
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