/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.services;

import dev.mmussatto.expensetracker.api.mappers.CategoryMapper;
import dev.mmussatto.expensetracker.api.model.CategoryDTO;
import dev.mmussatto.expensetracker.domain.Category;
import dev.mmussatto.expensetracker.domain.Transaction;
import dev.mmussatto.expensetracker.repositories.CategoryRepository;
import dev.mmussatto.expensetracker.services.exceptions.InvalidIdModificationException;
import dev.mmussatto.expensetracker.services.exceptions.ResourceAlreadyExistsException;
import dev.mmussatto.expensetracker.services.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryMapper categoryMapper, CategoryRepository categoryRepository) {
        this.categoryMapper = categoryMapper;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(category -> {
                    CategoryDTO categoryDTO = categoryMapper.convertToDTO(category);
                    categoryDTO.setPath("/api/categories/" + categoryDTO.getId());
                    return categoryDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .map(category -> {
                    CategoryDTO categoryDTO = categoryMapper.convertToDTO(category);
                    categoryDTO.setPath("/api/categories/" + categoryDTO.getId());
                    return categoryDTO;
                })
                .orElseThrow(() -> new ResourceNotFoundException("Category " + id + " not found!"));
    }

    @Override
    public CategoryDTO getCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .map(category -> {
                    CategoryDTO categoryDTO = categoryMapper.convertToDTO(category);
                    categoryDTO.setPath("/api/categories/" + categoryDTO.getId());
                    return categoryDTO;
                })
                .orElseThrow(() -> new ResourceNotFoundException("Category " + name + " not found!"));
    }

    @Override
    public CategoryDTO createNewCategory(CategoryDTO categoryDTO) {

        //Check is name is already in use
         categoryRepository.findByName(categoryDTO.getName()).ifPresent(category -> {
             throw new ResourceAlreadyExistsException("Category " + categoryDTO.getName() + " already exists.",
                     "/api/categories/" + category.getId());
         });

        //validation in the controller should prevent this exception
         if (categoryDTO.getId() != null)
             throw new RuntimeException("Id should be null.");


        return saveAndReturnDTO(categoryMapper.convertToEntity(categoryDTO)) ;
    }

    @Override
    public CategoryDTO updateCategoryById(Integer id, CategoryDTO categoryDTO) {

        categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category " + id + " not found!"));

        //Check if Ids are the same
//        if (categoryDTO.getId() != null && !Objects.equals(categoryDTO.getId(), id))
//            throw new InvalidIdModificationException(id.toString(), "/api/categories/" + id);

        if (categoryDTO.getId() != null)
            throw new RuntimeException("Id should be null.");

        //Check if name is already in use
        categoryRepository.findByName(categoryDTO.getName()).ifPresent(nameAlreadyInUse -> {
            throw new ResourceAlreadyExistsException("Category " + categoryDTO.getName() + " already exists.",
                    "/api/categories/" + nameAlreadyInUse.getId());
        });

        Category category = categoryMapper.convertToEntity(categoryDTO);
        category.setId(id);

        return saveAndReturnDTO(category);
    }

    @Override
    public CategoryDTO patchCategoryById (Integer id, CategoryDTO categoryDTO) {

        return categoryRepository.findById(id).map(category -> {

            //Check if Ids are the same
//            if (categoryDTO.getId() != null && !Objects.equals(categoryDTO.getId(), id))
//                throw new InvalidIdModificationException(id.toString(), "/api/categories/" + id);
            if (categoryDTO.getId() != null)
                throw new RuntimeException("Id should be null.");

            if (categoryDTO.getName() != null) {
                //Check if name is already in use
                categoryRepository.findByName(categoryDTO.getName()).ifPresent(nameAlreadyInUse -> {
                    throw new ResourceAlreadyExistsException("Category " + categoryDTO.getName() + " already exists.",
                            "/api/categories/" + nameAlreadyInUse.getId());
                });

                category.setName(categoryDTO.getName());
            }

            if (categoryDTO.getColor() != null)
                category.setColor(categoryDTO.getColor());

            if (categoryDTO.getTransactions() != null && categoryDTO.getTransactions().size() != 0)
                category.setTransactions(categoryDTO.getTransactions());

            return saveAndReturnDTO(category);

        }).orElseThrow(() -> new ResourceNotFoundException("Category " + id + " not found!"));
    }

    @Override
    public void deleteCategoryById(Integer id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public Set<Transaction> getTransactionsById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category " + id + " not found!"));

        return  category.getTransactions();
    }


    private CategoryDTO saveAndReturnDTO(Category category) {
        Category savedCategory = categoryRepository.save(category);

        CategoryDTO returnDTO = categoryMapper.convertToDTO(savedCategory);
        returnDTO.setPath("/api/categories/" + returnDTO.getId());

        return returnDTO;
    }
}
