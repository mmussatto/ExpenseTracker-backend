/*
 * Created by murilo.mussatto on 03/03/2023
 */

package dev.mmussatto.expensetracker.api.mappers;

import dev.mmussatto.expensetracker.api.model.CategoryDTO;
import dev.mmussatto.expensetracker.domain.Category;
import dev.mmussatto.expensetracker.domain.Color;
import dev.mmussatto.expensetracker.domain.Transaction;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CategoryMapperTest {

    public static final Integer ID = 1;
    public static final String NAME = "Test";
    public static final Color COLOR = Color.BLUE;
    public static final Set<Transaction> TRANSACTIONS = Stream.of(new Transaction(), new Transaction())
            .collect(Collectors.toSet());

    CategoryMapper categoryMapper = CategoryMapper.INSTANCE;

    @Test
    void categoryToCategoryDTO() {
        Category category = new Category(NAME, COLOR);
        category.setId(ID);
        category.setTransactions(TRANSACTIONS);

        CategoryDTO categoryDTO = categoryMapper.categoryToCategoryDTO(category);

        assertEquals(category.getId(), categoryDTO.getId());
        assertEquals(category.getName(), categoryDTO.getName());
        assertEquals(category.getColor(), categoryDTO.getColor());
        assertEquals(category.getTransactions(), categoryDTO.getTransactions());
    }

    @Test
    void categoryDTOToCategory() {

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(ID);
        categoryDTO.setName(NAME);
        categoryDTO.setColor(COLOR);
        categoryDTO.setTransactions(TRANSACTIONS);

        Category category = categoryMapper.categoryDTOToCategory(categoryDTO);

        assertEquals(categoryDTO.getId(), category.getId());
        assertEquals(categoryDTO.getName(), category.getName());
        assertEquals(categoryDTO.getColor(), category.getColor());
        assertEquals(categoryDTO.getTransactions(), category.getTransactions());
    }
}