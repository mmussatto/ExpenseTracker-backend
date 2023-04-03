/*
 * Created by murilo.mussatto on 03/04/2023
 */


package dev.mmussatto.expensetracker.entities.vendor;

import dev.mmussatto.expensetracker.entities.transaction.Transaction;
import dev.mmussatto.expensetracker.entities.vendor.onlinestore.OnlineStore;
import dev.mmussatto.expensetracker.entities.vendor.onlinestore.OnlineStoreDTO;
import dev.mmussatto.expensetracker.entities.vendor.physicalstore.PhysicalStore;
import dev.mmussatto.expensetracker.entities.vendor.physicalstore.PhysicalStoreDTO;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VendorMapperTest {

    public static final Integer ID = 1;
    public static final String NAME = "Test";
    public static final String URL  = "www.test.com";
    public static final String ADDRESS = "123 Test St.";
    public static final List<Transaction> TRANSACTIONS = Stream.of(new Transaction(), new Transaction()).collect(Collectors.toList());

    VendorMapper vendorMapper = VendorMapper.INSTANCE;


    @Test
    void convertPhysicalStoreToDTO() {

        PhysicalStore entity = new PhysicalStore(NAME, ADDRESS);
        entity.setId(ID);
        entity.setTransactions(TRANSACTIONS);

        PhysicalStoreDTO dto = vendorMapper.convertToDTO(entity);

        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getAddress(), dto.getAddress());
        assertEquals(entity.getTransactions(), dto.getTransactions());
    }

    @Test
    void convertPhysicalStoreDTOToEntity() {

        PhysicalStoreDTO dto = new PhysicalStoreDTO(NAME, ADDRESS);
        dto.setId(ID);
        dto.setTransactions(TRANSACTIONS);

        PhysicalStore entity = vendorMapper.convertToEntity(dto);

        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getAddress(), entity.getAddress());
        assertEquals(dto.getTransactions(), entity.getTransactions());
    }

    @Test
    void testConvertOnlineStoreToDTO() {

        OnlineStore entity = new OnlineStore(NAME, URL);
        entity.setId(ID);
        entity.setTransactions(TRANSACTIONS);

        OnlineStoreDTO dto = vendorMapper.convertToDTO(entity);

        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getUrl(), dto.getUrl());
        assertEquals(entity.getTransactions(), dto.getTransactions());
    }

    @Test
    void testConvertOnlineStoreDTOToEntity() {

        OnlineStoreDTO dto = new OnlineStoreDTO(NAME, ADDRESS);
        dto.setId(ID);
        dto.setTransactions(TRANSACTIONS);

        OnlineStore entity = vendorMapper.convertToEntity(dto);

        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getUrl(), entity.getUrl());
        assertEquals(dto.getTransactions(), entity.getTransactions());
    }


}