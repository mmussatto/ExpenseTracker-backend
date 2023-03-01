/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.services;

import dev.mmussatto.expensetracker.api.mappers.CategoryMapper;
import dev.mmussatto.expensetracker.api.model.CategoryDTO;
import dev.mmussatto.expensetracker.domain.Category;
import dev.mmussatto.expensetracker.repositories.CategoryRepository;
import dev.mmussatto.expensetracker.services.exceptions.ResourceAlreadyExistsException;
import dev.mmussatto.expensetracker.services.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
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
                    CategoryDTO categoryDTO = categoryMapper.categoryToCategoryDTO(category);
                    categoryDTO.setUrl("/api/categories/" + categoryDTO.getId());
                    return categoryDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .map(category -> {
                    CategoryDTO categoryDTO = categoryMapper.categoryToCategoryDTO(category);
                    categoryDTO.setUrl("/api/categories/" + categoryDTO.getId());
                    return categoryDTO;
                })
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public CategoryDTO getCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .map(category -> {
                    CategoryDTO categoryDTO = categoryMapper.categoryToCategoryDTO(category);
                    categoryDTO.setUrl("/api/categories/" + categoryDTO.getId());
                    return categoryDTO;
                })
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public CategoryDTO createNewCategory(CategoryDTO categoryDTO) {
         categoryRepository.findByName(categoryDTO.getName()).ifPresent(category -> {
             throw new ResourceAlreadyExistsException("/api/categories/" + category.getId());
         });


        return saveAndReturnDTO(categoryMapper.categoryDTOToCategory(categoryDTO)) ;
    }

    @Override
    public CategoryDTO saveCategoryById(Integer id, CategoryDTO categoryDTO) {
        Category category = categoryMapper.categoryDTOToCategory(categoryDTO);
        category.setId(id);

        return saveAndReturnDTO(category);
    }

    @Override
    public void deleteCategoryById(Integer id) {
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteCategoryByName(String name) {
        categoryRepository.deleteByName(name);
    }


    private CategoryDTO saveAndReturnDTO(Category category) {
        Category savedCategory = categoryRepository.save(category);

        CategoryDTO returnDTO = categoryMapper.categoryToCategoryDTO(savedCategory);
        returnDTO.setUrl("/api/categories/" + returnDTO.getId());

        return returnDTO;
    }
}
