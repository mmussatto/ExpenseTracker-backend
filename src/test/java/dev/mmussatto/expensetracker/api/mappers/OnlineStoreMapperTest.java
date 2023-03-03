/*
 * Created by murilo.mussatto on 03/03/2023
 */

package dev.mmussatto.expensetracker.api.mappers;

import dev.mmussatto.expensetracker.api.model.OnlineStoreDTO;
import dev.mmussatto.expensetracker.domain.OnlineStore;
import dev.mmussatto.expensetracker.domain.Transaction;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OnlineStoreMapperTest {

    public static final Integer ID = 1;
    public static final String NAME = "Test";
    public static final Set<Transaction> TRANSACTIONS = Stream.of(new Transaction(), new Transaction())
            .collect(Collectors.toSet());
    public static final String URL = "www.somewebsite.com";

    OnlineStoreMapper onlineStoreMapper = OnlineStoreMapper.INSTANCE;

    @Test
    void onlineStoreToOnlineStoreDTO() {
        OnlineStore onlineStore = new OnlineStore(NAME, URL);
        onlineStore.setId(ID);
        onlineStore.setTransactions(TRANSACTIONS);

        OnlineStoreDTO onlineStoreDTO = onlineStoreMapper.onlineStoreToOnlineStoreDTO(onlineStore);

        assertEquals(onlineStore.getId(), onlineStoreDTO.getId());
        assertEquals(onlineStore.getName(), onlineStoreDTO.getName());
        assertEquals(onlineStore.getUrl(), onlineStoreDTO.getUrl());
        assertEquals(onlineStore.getTransactions(), onlineStoreDTO.getTransactions());
    }

    @Test
    void onlineStoreDTOToOnlineStore() {

        OnlineStoreDTO onlineStoreDTO = new OnlineStoreDTO();
        onlineStoreDTO.setId(ID);
        onlineStoreDTO.setName(NAME);
        onlineStoreDTO.setUrl(URL);
        onlineStoreDTO.setTransactions(TRANSACTIONS);

        OnlineStore onlineStore = onlineStoreMapper.onlineStoreDTOToOnlineStore(onlineStoreDTO);

        assertEquals(onlineStoreDTO.getId(), onlineStore.getId());
        assertEquals(onlineStoreDTO.getName(), onlineStore.getName());
        assertEquals(onlineStoreDTO.getUrl(), onlineStore.getUrl());
        assertEquals(onlineStoreDTO.getTransactions(), onlineStore.getTransactions());
    }
}