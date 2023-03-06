/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.services;

import dev.mmussatto.expensetracker.api.model.CategoryDTO;
import dev.mmussatto.expensetracker.domain.Transaction;

import java.util.List;
import java.util.Set;

public interface CategoryService {

    List<CategoryDTO> getAllCategories ();

    CategoryDTO getCategoryById (Integer id);

    CategoryDTO getCategoryByName (String name);

    CategoryDTO createNewCategory (CategoryDTO categoryDTO);

    CategoryDTO updateCategoryById (Integer id, CategoryDTO categoryDTO);

    CategoryDTO updateCategoryByName (String name, CategoryDTO categoryDTO);

    CategoryDTO patchCategoryById (Integer id, CategoryDTO categoryDTO);

    void deleteCategoryById (Integer id);

    void deleteCategoryByName (String name);

    Set<Transaction> getTransactionsById(Integer id);
}
