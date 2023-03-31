/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.entities.category;

import dev.mmussatto.expensetracker.entities.helpers.ListDTO;
import dev.mmussatto.expensetracker.entities.helpers.PageDTO;
import dev.mmussatto.expensetracker.entities.transaction.Transaction;
import dev.mmussatto.expensetracker.entities.transaction.TransactionDTO;
import dev.mmussatto.expensetracker.entities.transaction.TransactionMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Categories", description = "CRUD API for Category entity")
@Validated
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    public CategoryController(CategoryService categoryService, CategoryMapper categoryMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }



    @Operation(summary = "Get all categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the categories", useReturnTypeSchema = true)
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ListDTO<CategoryDTO> getAllCategories () {

        //Convert to DTO
        List<CategoryDTO> list = categoryService.getAllCategories()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new ListDTO<>(list);
    }


    @Operation(summary = "Get a category by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the category", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDTO getCategoryById (@PathVariable Integer id) {
        return convertToDTO(categoryService.getCategoryById(id));
    }


    @Operation(summary = "Get a category by its name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the category", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Invalid name supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    @GetMapping("/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDTO getCategoryByName (@PathVariable final String name) {
        return convertToDTO(categoryService.getCategoryByName(name));
    }


    @Operation(summary = "Create category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Invalid body supplied", content = @Content),
            @ApiResponse(responseCode = "409", description = "Name supplied is already in use", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(CategoryDTO.allFieldsValidation.class)
    public CategoryDTO createNewCategory (@Valid @RequestBody CategoryDTO categoryDTO) {
        Category entity = convertToEntity(categoryDTO);
        return convertToDTO(categoryService.createNewCategory(entity));
    }


    @Operation(summary = "Update category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Invalid body supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Name supplied is already in use", content = @Content)
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Validated(CategoryDTO.allFieldsValidation.class)
    public CategoryDTO updateCategoryById (@PathVariable final Integer id, @Valid @RequestBody CategoryDTO categoryDTO) {
        Category entity = convertToEntity(categoryDTO);
        return convertToDTO(categoryService.updateCategoryById(id, entity));
    }


    @Operation(summary = "Patch category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category patched", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Invalid body supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Name supplied is already in use", content = @Content)
    })
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Validated(CategoryDTO.onlyIdValidation.class)
    public CategoryDTO patchCategoryById (@PathVariable final Integer id, @Valid @RequestBody CategoryDTO categoryDTO) {
        Category entity = convertToEntity(categoryDTO);
        return convertToDTO(categoryService.patchCategoryById(id, entity));
    }


    @Operation(summary = "Delete category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById (@PathVariable final Integer id) {
        categoryService.deleteCategoryById(id);
    }


    //Categories' Transactions
    @Operation(summary = "Get all transactions from a category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the category's transactions", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    @GetMapping("/{id}/transactions")
    @ResponseStatus(HttpStatus.OK)
    public PageDTO<TransactionDTO> getCategoryTransactionsById (@PathVariable final Integer id,
                                                                @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                                @RequestParam(value = "size", defaultValue = "1", required = false) int size) {
        Page<Transaction> paginatedTransactions = categoryService.getTransactionsById(id, page, size);

        PageDTO<TransactionDTO> returnPage = new PageDTO<>();

        returnPage.setContent(paginatedTransactions.getContent()
                .stream()
                .map(transaction -> {
                    TransactionDTO dto = TransactionMapper.INSTANCE.convertToDTO(transaction);
                    dto.setPath("/api/transactions/" + dto.getId());
                    return dto;
                })
                .collect(Collectors.toList()));

        returnPage.setPageNo(paginatedTransactions.getNumber());
        returnPage.setPageSize(paginatedTransactions.getSize());
        returnPage.setTotalElements(paginatedTransactions.getTotalElements());
        returnPage.setTotalPages(paginatedTransactions.getTotalPages());

        if (paginatedTransactions.hasNext())
            returnPage.setNextPage(String.format("/api/categories/%d/transactions?page=%d&size=%d",
                    id, paginatedTransactions.getNumber()+1, paginatedTransactions.getSize()));

        if (paginatedTransactions.hasPrevious())
            returnPage.setPreviousPage(String.format("/api/categories/%d/transactions?page=%d&size=%d",
                    id, paginatedTransactions.getNumber()-1, paginatedTransactions.getSize()));

        return returnPage;
    }


    //Mappers
    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = categoryMapper.convertToDTO(category);
        dto.setPath("/api/categories/" + dto.getId());
        return dto;
    }

    private Category convertToEntity (CategoryDTO categoryDTO) {
         return categoryMapper.convertToEntity(categoryDTO);
    }

}
