/*
 * Created by murilo.mussatto on 03/04/2023
 */

/*
 * Created by murilo.mussatto on 01/03/2023
 */

package dev.mmussatto.expensetracker.entities.category;

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
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 1;
    public static final String NAME = "Test name";
    public static final Color COLOR = Color.BLUE;
    public static final int ID = 1;


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

        //Create categories
        Category c1 = new Category("C1", Color.BLUE);
        c1.setId(1);

        Category c2 = new Category("C2", Color.RED);
        c2.setId(2);

        //Create DTOs
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
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getCategoryById() throws Exception {

        Category savedEntity = new Category(NAME, COLOR);
        savedEntity.setId(ID);

        CategoryDTO returnedDTO = new CategoryDTO(savedEntity.getName(), savedEntity.getColor());
        returnedDTO.setId(savedEntity.getId());
        returnedDTO.setPath("/api/categories/" + returnedDTO.getId() );

        when(categoryService.getCategoryById(savedEntity.getId())).thenReturn(savedEntity);
        when(categoryMapper.convertToDTO(savedEntity)).thenReturn(returnedDTO);


        mockMvc.perform(get("/api/categories/{id}", savedEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String retString = result.getResponse().getContentAsString();
                    CategoryDTO objFromJson = objectMapper.readValue(retString, CategoryDTO.class);
                    assertEquals(returnedDTO, objFromJson);
                });
    }

    @Test
    void getCategoryById_NotFound() throws Exception {

        when(categoryService.getCategoryById(ID)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/categories/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof  ResourceNotFoundException));
    }

    @Test
    void getCategoryByName() throws Exception {

        Category savedEntity = new Category(NAME, COLOR);
        savedEntity.setId(ID);

        CategoryDTO returnedDTO = new CategoryDTO(savedEntity.getName(), savedEntity.getColor());
        returnedDTO.setId(savedEntity.getId());
        returnedDTO.setPath("/api/categories/" + returnedDTO.getId() );


        when(categoryService.getCategoryByName(savedEntity.getName())).thenReturn(savedEntity);
        when(categoryMapper.convertToDTO(savedEntity)).thenReturn(returnedDTO);


        mockMvc.perform(get("/api/categories/name/{name}", returnedDTO.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String retString = result.getResponse().getContentAsString();
                    CategoryDTO objFromJson = objectMapper.readValue(retString, CategoryDTO.class);
                    assertEquals(returnedDTO, objFromJson);
                });
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

        CategoryDTO passedDTO = new CategoryDTO(NAME, COLOR);

        Category toSaveEntity = new Category(passedDTO.getName(), passedDTO.getColor());

        Category savedEntity = new Category(toSaveEntity.getName(), toSaveEntity.getColor());
        savedEntity.setId(ID);

        CategoryDTO returnedDTO = new CategoryDTO(savedEntity.getName(), savedEntity.getColor());
        returnedDTO.setId(savedEntity.getId());
        returnedDTO.setPath("/api/categories/" + returnedDTO.getId());


        when(categoryMapper.convertToEntity(passedDTO)).thenReturn(toSaveEntity);
        when(categoryService.createNewCategory(toSaveEntity)).thenReturn(savedEntity);
        when(categoryMapper.convertToDTO(savedEntity)).thenReturn(returnedDTO);

        mockMvc.perform(post("/api/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String retString = result.getResponse().getContentAsString();
                    CategoryDTO objFromJson = objectMapper.readValue(retString, CategoryDTO.class);
                    assertEquals(returnedDTO, objFromJson);
                });
    }

    @Test
    void createNewCategory_BodyIdNotNull() throws Exception {

        CategoryDTO passedDTO = new CategoryDTO(NAME, COLOR);
        passedDTO.setId(ID);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
    }

    @Test
    void createNewCategory_NameAlreadyExists() throws Exception {

        CategoryDTO passedDTO = new CategoryDTO(NAME, COLOR);

        Category toSaveEntity = new Category(passedDTO.getName(), passedDTO.getColor());

        when(categoryMapper.convertToEntity(passedDTO)).thenReturn(toSaveEntity);
        when(categoryService.createNewCategory(toSaveEntity)).thenThrow(ResourceAlreadyExistsException.class);


        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceAlreadyExistsException));
    }

    @Test
    void updateCategoryById() throws Exception {

        Integer savedId = ID;

        CategoryDTO passedDTO = new CategoryDTO("Update Test", Color.RED);

        Category toUpdateEntity = new Category(passedDTO.getName(), passedDTO.getColor());

        Category updatedEntity = new Category(toUpdateEntity.getName(), toUpdateEntity.getColor());
        updatedEntity.setId(savedId);

        CategoryDTO returnedDTO = new CategoryDTO(updatedEntity.getName(), updatedEntity.getColor());
        returnedDTO.setId(updatedEntity.getId());
        returnedDTO.setPath("/api/categories/" + returnedDTO.getId());


        when(categoryMapper.convertToEntity(passedDTO)).thenReturn(toUpdateEntity);
        when(categoryService.updateCategoryById(savedId, toUpdateEntity)).thenReturn(updatedEntity);
        when(categoryMapper.convertToDTO(updatedEntity)).thenReturn(returnedDTO);

        mockMvc.perform(put("/api/categories/{id}", savedId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String retString = result.getResponse().getContentAsString();
                    CategoryDTO objFromJson = objectMapper.readValue(retString, CategoryDTO.class);
                    assertEquals(returnedDTO, objFromJson);
                });
    }

    @Test
    void updateCategoryById_NotFound() throws Exception {

        Integer notFoundId = 123;

        CategoryDTO passedDTO = new CategoryDTO("Update Test", Color.RED);

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

        mockMvc.perform(put("/api/categories/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
    }

    @Test
    void updateCategoryById_BodyIdNotNull() throws Exception {

        CategoryDTO passedDTO = new CategoryDTO("Update Test", Color.RED);
        passedDTO.setId(ID);


        mockMvc.perform(put("/api/categories/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
    }

    @Test
    void updateCategoryById_MissingNameField() throws Exception {

        CategoryDTO passedDTO = new CategoryDTO();
        //missing name
        passedDTO.setColor(COLOR);

        mockMvc.perform(put("/api/categories/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
    }

    @Test
    void patchCategoryById() throws Exception {

        Integer savedId = ID;

        CategoryDTO passedDTO = new CategoryDTO("Patch Test", COLOR);

        Category toPatchEntity = new Category(passedDTO.getName(), passedDTO.getColor());

        Category patchedEntity = new Category(toPatchEntity.getName(), toPatchEntity.getColor());
        patchedEntity.setId(savedId);

        CategoryDTO returnedDTO = new CategoryDTO(patchedEntity.getName(), patchedEntity.getColor());
        returnedDTO.setId(patchedEntity.getId());
        returnedDTO.setPath("/api/categories/" + returnedDTO.getId());


        when(categoryMapper.convertToEntity(passedDTO)).thenReturn(toPatchEntity);
        when(categoryService.patchCategoryById(savedId, toPatchEntity)).thenReturn(patchedEntity);
        when(categoryMapper.convertToDTO(patchedEntity)).thenReturn(returnedDTO);


        mockMvc.perform(patch("/api/categories/{id}", savedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String retString = result.getResponse().getContentAsString();
                    CategoryDTO objFromJson = objectMapper.readValue(retString, CategoryDTO.class);
                    assertEquals(returnedDTO, objFromJson);
                });
    }

    @Test
    void patchCategoryById_NotFound() throws Exception {

        Integer notFoundId = 123;

        CategoryDTO passedDTO = new CategoryDTO("Patch Test", COLOR);

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

        CategoryDTO passedDTO = new CategoryDTO("Patch Test", COLOR);
        passedDTO.setId(ID);

        mockMvc.perform(patch("/api/categories/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passedDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException));
    }

    @Test
    void deleteCategoryById() throws Exception {

        mockMvc.perform(delete("/api/categories/{id}", ID)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(categoryService, times(ID)).deleteCategoryById(anyInt());
    }

    @Test
    void deleteCategoryById_NotFound() throws Exception {

        doThrow(ResourceNotFoundException.class).when(categoryService).deleteCategoryById(anyInt());

        mockMvc.perform(delete("/api/categories/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));

        verify(categoryService, times(ID)).deleteCategoryById(anyInt());
    }

    @Test
    void getTransactionsByCategoryId() throws Exception {

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

        Integer categoryId = ID;

        Pageable pageable = PageRequest.of(1, DEFAULT_SIZE, Sort.by("date"));
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), transactions.size());

        Page<Transaction> pagedTransactions = new PageImpl<>(
                transactions.subList(start, end), pageable, transactions.size());


        when(categoryService.getTransactionsByCategoryId(categoryId, 1, DEFAULT_SIZE)).thenReturn(pagedTransactions);

        mockMvc.perform(get("/api/categories/{id}/transactions", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNo", equalTo(1)))
                .andExpect(jsonPath("$.pageSize", equalTo(DEFAULT_SIZE)))
                .andExpect(jsonPath("$.totalElements", equalTo(transactions.size())))
                .andExpect(jsonPath("$.nextPage", equalTo("/api/categories/1/transactions?page=2&size=1")))
                .andExpect(jsonPath("$.previousPage", equalTo("/api/categories/1/transactions?page=0&size=1")))
                .andExpect(jsonPath("$.content", hasSize(DEFAULT_SIZE)))
                .andDo(print());
    }

    @Test
    void getTransactionsByCategoryId_NotFound() throws Exception {

        Integer notFoundID = 123;

        when(categoryService.getTransactionsByCategoryId(notFoundID, DEFAULT_PAGE, DEFAULT_SIZE))
                .thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/categories/{id}/transactions", notFoundID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));


    }
}