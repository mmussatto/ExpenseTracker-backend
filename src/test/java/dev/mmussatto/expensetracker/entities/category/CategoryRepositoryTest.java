/*
 * Created by murilo.mussatto on 03/04/2023
 */

/*
 * Created by murilo.mussatto on 27/02/2023
 */

package dev.mmussatto.expensetracker.entities.category;

import dev.mmussatto.expensetracker.entities.helpers.Color;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    CategoryRepository categoryRepository;


    @Test
    void findByName() {
        Category c1 = new Category("Test 1", Color.BLUE);
        testEntityManager.persist(c1);

        Category c2 = new Category("Test 2", Color.RED);
        testEntityManager.persist(c2);


        Category test = categoryRepository.findByName("Test 1").get();

        assertEquals(c1.getId(), test.getId());
    }

    @Test
    void deleteByName() {
        Category c1 = new Category("Test 1", Color.BLUE);
        testEntityManager.persist(c1);

        categoryRepository.deleteByName("Test 1");

        assertFalse(categoryRepository.findByName("Test 1").isPresent());
    }

    @Test
    void prevent_null() {
        Category category = new Category();
        //testEntityManager.persist(category);

        assertThrows(ConstraintViolationException.class, () -> testEntityManager.persist(category));

    }
}