/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.entities.category;

import dev.mmussatto.expensetracker.entities.transaction.Transaction;
import dev.mmussatto.expensetracker.exceptions.ResourceAlreadyExistsException;
import dev.mmussatto.expensetracker.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category " + id + " not found!"));
    }

    @Override
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Category " + name + " not found!"));
    }

    @Override
    public Category createNewCategory(Category category) {

        checkIfNameIsAlreadyInUse(category);

        return categoryRepository.save(category) ;
    }

    @Override
    public Category updateCategoryById(Integer id, Category category) {

        categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category " + id + " not found!"));


        checkIfNameIsAlreadyInUse(category);

        category.setId(id);

        return categoryRepository.save(category);
    }

    @Override
    public Category patchCategoryById (Integer id, Category category) {

        return categoryRepository.findById(id).map(savedCategory -> {

            if (category.getName() != null) {
                checkIfNameIsAlreadyInUse(category);
                savedCategory.setName(category.getName());
            }

            if (category.getColor() != null)
                savedCategory.setColor(category.getColor());

            return categoryRepository.save(savedCategory);

        }).orElseThrow(() -> new ResourceNotFoundException("Category " + id + " not found!"));
    }

    @Override
    public void deleteCategoryById(Integer id) {
        categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category " + id + " not found!"));

        categoryRepository.deleteById(id);
    }

    @Override
    public Page<Transaction> getTransactionsByCategoryId(Integer id, int page, int size) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category " + id + " not found!"));

        List<Transaction>  transactions = category.getTransactions();

        Pageable pageable = PageRequest.of(page, size, Sort.by("date"));
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), transactions.size());

        return new PageImpl<Transaction>(transactions.subList(start, end), pageable, transactions.size());
    }

    private void checkIfNameIsAlreadyInUse(Category category) {
        categoryRepository.findByName(category.getName()).ifPresent(savedCategory -> {
            throw new ResourceAlreadyExistsException("Category " + category.getName() + " already exists.",
                    "/api/categories/" + savedCategory.getId());
        });
    }
}
