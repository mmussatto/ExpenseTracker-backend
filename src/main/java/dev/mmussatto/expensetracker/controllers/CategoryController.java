/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.controllers;

import dev.mmussatto.expensetracker.api.mappers.CategoryMapper;
import dev.mmussatto.expensetracker.api.mappers.TransactionMapper;
import dev.mmussatto.expensetracker.api.model.CategoryDTO;
import dev.mmussatto.expensetracker.api.model.ListDTO;
import dev.mmussatto.expensetracker.api.model.TransactionDTO;
import dev.mmussatto.expensetracker.domain.Category;
import dev.mmussatto.expensetracker.domain.Transaction;
import dev.mmussatto.expensetracker.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    //Categories
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

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDTO getCategoryById (@PathVariable Integer id) {
        return convertToDTO(categoryService.getCategoryById(id));
    }

    @GetMapping("/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDTO getCategoryByName (@PathVariable final String name) {
        return convertToDTO(categoryService.getCategoryByName(name));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(CategoryDTO.allFieldsValidation.class)
    public CategoryDTO createNewCategory (@Valid @RequestBody CategoryDTO categoryDTO) {
        Category entity = convertToEntity(categoryDTO);
        return convertToDTO(categoryService.createNewCategory(entity));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Validated(CategoryDTO.allFieldsValidation.class)
    public CategoryDTO updateCategoryById (@PathVariable final Integer id, @Valid @RequestBody CategoryDTO categoryDTO) {
        Category entity = convertToEntity(categoryDTO);
        return convertToDTO(categoryService.updateCategoryById(id, entity));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Validated(CategoryDTO.onlyIdValidation.class)
    public CategoryDTO patchCategoryById (@PathVariable final Integer id, @Valid @RequestBody CategoryDTO categoryDTO) {
        Category entity = convertToEntity(categoryDTO);
        return convertToDTO(categoryService.patchCategoryById(id, entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById (@PathVariable final Integer id) {
        categoryService.deleteCategoryById(id);
    }


    //Categories' Transactions
    @GetMapping("/{id}/transactions")
    @ResponseStatus(HttpStatus.OK)
    public ListDTO<TransactionDTO> getCategoryTransactionsById (@PathVariable final Integer id) {
        Set<Transaction> transactions = categoryService.getTransactionsById(id);

        return new ListDTO<>(transactions.stream()
                .map(transaction -> {
                    TransactionDTO dto = TransactionMapper.INSTANCE.convertToDTO(transaction);
                    dto.setPath("/api/transactions/" + dto.getId());
                    return dto;
                }).collect(Collectors.toList()));
    }

    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = categoryMapper.convertToDTO(category);
        dto.setPath("/api/categories/" + dto.getId());
        return dto;
    }

    private Category convertToEntity (CategoryDTO categoryDTO) {
         return categoryMapper.convertToEntity(categoryDTO);
    }

}
