/*
 * Created by murilo.mussatto on 27/02/2023
 */

package dev.mmussatto.expensetracker.tag;

import dev.mmussatto.expensetracker.entities.helpers.Color;
import dev.mmussatto.expensetracker.entities.tag.Tag;
import dev.mmussatto.expensetracker.entities.tag.TagRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class TagRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    TagRepository tagRepository;


    @Test
    void findByName() {
        Tag t1 = new Tag("Test 1", Color.BLUE);
        testEntityManager.persist(t1);

        Tag t2 = new Tag("Test 2", Color.RED);
        testEntityManager.persist(t2);


        Tag test = tagRepository.findByName("Test 1").get();

        assertEquals(t1.getId(), test.getId());
    }

}