/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.controllers;

import dev.mmussatto.expensetracker.api.model.CategoryDTO;
import dev.mmussatto.expensetracker.api.model.CategoryListDTO;
import dev.mmussatto.expensetracker.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CategoryListDTO getAllCategories () {
        return new CategoryListDTO(categoryService.getAllCategories());
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
    public CategoryDTO createNewCategory (@Valid @RequestBody CategoryDTO categoryDTO) {
        return categoryService.createNewCategory(categoryDTO);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDTO updateCategoryById (@PathVariable final Integer id, @Valid @RequestBody CategoryDTO categoryDTO) {
        return categoryService.updateCategoryById(id, categoryDTO);
    }

    @PutMapping("/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDTO updateCategoryByName (@PathVariable final String name, @Valid @RequestBody CategoryDTO categoryDTO) {
        return categoryService.updateCategoryByName(name, categoryDTO);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDTO patchCategoryById (@PathVariable final Integer id, @RequestBody CategoryDTO categoryDTO) {
        return categoryService.patchCategoryById(id, categoryDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCategoryById (@PathVariable final Integer id) {
        categoryService.deleteCategoryById(id);
    }

    @DeleteMapping("/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCategoryById (@PathVariable final String name) {
        categoryService.deleteCategoryByName(name);
    }


}
