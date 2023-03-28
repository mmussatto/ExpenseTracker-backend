/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.entities.category;

import dev.mmussatto.expensetracker.entities.transaction.Transaction;

import java.util.List;
import java.util.Set;

public interface CategoryService {

    List<Category> getAllCategories ();

    Category getCategoryById (Integer id);

    Category getCategoryByName (String name);

    Category createNewCategory (Category category);

    Category updateCategoryById (Integer id, Category category);

    Category patchCategoryById (Integer id, Category category);

    void deleteCategoryById (Integer id);

    Set<Transaction> getTransactionsById(Integer id);
}
