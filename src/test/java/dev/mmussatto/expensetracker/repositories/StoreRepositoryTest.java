/*
 * Created by murilo.mussatto on 27/02/2023
 */

package dev.mmussatto.expensetracker.repositories;

import dev.mmussatto.expensetracker.domain.OnlineStore;
import dev.mmussatto.expensetracker.domain.PhysicalStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class StoreRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private StoreRepository<OnlineStore> onlineStoreStoreRepository;

    @Autowired
    StoreRepository<PhysicalStore> physicalStoreStoreRepository;


    @Test
    void findByName() {
        OnlineStore os1 = new OnlineStore("Test 1", "https://test1.com");
        testEntityManager.persist(os1);

        OnlineStore os2 = new OnlineStore("Test 2", "https://www.test2.com.br/");
        testEntityManager.persist(os2);

        PhysicalStore ps1 = new PhysicalStore("Test 3", "Av. Test 3, 1515");
        testEntityManager.persist(ps1);

        PhysicalStore ps2 = new PhysicalStore("Test 4", "Av. Test 4, 1616");
        testEntityManager.persist(ps2);

        OnlineStore osTest = onlineStoreStoreRepository.findByName("Test 1").get();
        PhysicalStore psTest = physicalStoreStoreRepository.findByName("Test 3").get();


        assertEquals(os1.getId(), osTest.getId());
        assertEquals(ps1.getId(), psTest.getId());
    }
}