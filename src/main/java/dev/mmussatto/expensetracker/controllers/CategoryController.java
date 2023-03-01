/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.controllers;

import dev.mmussatto.expensetracker.api.model.CategoryDTO;
import dev.mmussatto.expensetracker.api.model.CategoryListDTO;
import dev.mmussatto.expensetracker.services.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDTO getCategoryByName (@PathVariable final String name) {
        return categoryService.getCategoryByName(name);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDTO getCategoryById (@PathVariable Integer id) {
        return categoryService.getCategoryById(id);
    }


}
