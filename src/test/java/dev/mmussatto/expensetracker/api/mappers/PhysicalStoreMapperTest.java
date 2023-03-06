/*
 * Created by murilo.mussatto on 03/03/2023
 */

package dev.mmussatto.expensetracker.api.mappers;

import dev.mmussatto.expensetracker.api.model.PhysicalStoreDTO;
import dev.mmussatto.expensetracker.domain.PhysicalStore;
import dev.mmussatto.expensetracker.domain.Transaction;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PhysicalStoreMapperTest {

    public static final Integer ID = 1;
    public static final String NAME = "Test";
    public static final Set<Transaction> TRANSACTIONS = Stream.of(new Transaction(), new Transaction())
            .collect(Collectors.toSet());
    public static final String ADDRESS = "Nowhere Av.";

    PhysicalStoreMapper physicalStoreMapper = PhysicalStoreMapper.INSTANCE;

    @Test
    void physicalStoreToPhysicalStoreDTO() {
        PhysicalStore physicalStore = new PhysicalStore(NAME, ADDRESS);
        physicalStore.setId(ID);
        physicalStore.setTransactions(TRANSACTIONS);

        PhysicalStoreDTO physicalStoreDTO = physicalStoreMapper.convertToDTO(physicalStore);

        assertEquals(physicalStore.getId(), physicalStoreDTO.getId());
        assertEquals(physicalStore.getName(), physicalStoreDTO.getName());
        assertEquals(physicalStore.getAddress(), physicalStoreDTO.getAddress());
        assertEquals(physicalStore.getTransactions(), physicalStoreDTO.getTransactions());
    }

    @Test
    void physicalStoreDTOToPhysicalStore() {

        PhysicalStoreDTO physicalStoreDTO = new PhysicalStoreDTO();
        physicalStoreDTO.setId(ID);
        physicalStoreDTO.setName(NAME);
        physicalStoreDTO.setAddress(ADDRESS);
        physicalStoreDTO.setTransactions(TRANSACTIONS);

        PhysicalStore physicalStore = physicalStoreMapper.convertToEntity(physicalStoreDTO);

        assertEquals(physicalStoreDTO.getId(), physicalStore.getId());
        assertEquals(physicalStoreDTO.getName(), physicalStore.getName());
        assertEquals(physicalStoreDTO.getAddress(), physicalStore.getAddress());
        assertEquals(physicalStoreDTO.getTransactions(), physicalStore.getTransactions());
    }
}