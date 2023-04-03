/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.entities.category;

import dev.mmussatto.expensetracker.entities.transaction.Transaction;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {

    List<Category> getAllCategories ();

    Category getCategoryById (Integer id);

    Category getCategoryByName (String name);

    Category createNewCategory (Category category);

    Category updateCategoryById (Integer id, Category category);

    Category patchCategoryById (Integer id, Category category);

    void deleteCategoryById (Integer id);

    Page<Transaction> getTransactionsByCategoryId(Integer id, int page, int size);
}
