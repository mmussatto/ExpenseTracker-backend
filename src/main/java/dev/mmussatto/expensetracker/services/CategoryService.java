/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.services;

import dev.mmussatto.expensetracker.api.model.CategoryDTO;

import java.util.List;

public interface CategoryService {

    List<CategoryDTO> getAllCategories ();

    CategoryDTO getCategoryById (Integer id);

    CategoryDTO getCategoryByName (String name);

    CategoryDTO createNewCategory (CategoryDTO categoryDTO);

    CategoryDTO saveCategoryById (Integer id, CategoryDTO categoryDTO);

    void deleteCategoryById (Integer id);

    void deleteCategoryByName (String name);
}
