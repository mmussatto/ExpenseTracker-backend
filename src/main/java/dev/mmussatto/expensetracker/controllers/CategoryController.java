/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.controllers;

import dev.mmussatto.expensetracker.api.model.CategoryDTO;
import dev.mmussatto.expensetracker.api.model.ListDTO;
import dev.mmussatto.expensetracker.domain.Transaction;
import dev.mmussatto.expensetracker.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Validated
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    //Categories
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ListDTO<CategoryDTO> getAllCategories () {
        return new ListDTO<>(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDTO getCategoryById (@PathVariable Integer id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping("/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDTO getCategoryByName (@PathVariable final String name) {
        return categoryService.getCategoryByName(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(CategoryDTO.allFieldsValidation.class)
    public CategoryDTO createNewCategory (@Valid @RequestBody CategoryDTO categoryDTO) {
        return categoryService.createNewCategory(categoryDTO);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Validated(CategoryDTO.allFieldsValidation.class)
    public CategoryDTO updateCategoryById (@PathVariable final Integer id, @Valid @RequestBody CategoryDTO categoryDTO) {
        return categoryService.updateCategoryById(id, categoryDTO);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Validated(CategoryDTO.onlyIdValidation.class)
    public CategoryDTO patchCategoryById (@PathVariable final Integer id, @Valid @RequestBody CategoryDTO categoryDTO) {
        return categoryService.patchCategoryById(id, categoryDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById (@PathVariable final Integer id) {
        categoryService.deleteCategoryById(id);
    }


    //Categories' Transactions
    @GetMapping("/{id}/transactions")
    @ResponseStatus(HttpStatus.OK)
    public Set<Transaction> getCategoryTransactionsById (@PathVariable final Integer id) {
        return categoryService.getTransactionsById(id);
    }

}
