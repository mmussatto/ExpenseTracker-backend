/*
 * Created by murilo.mussatto on 15/03/2023
 */

package dev.mmussatto.expensetracker.entities.vendor;

import dev.mmussatto.expensetracker.entities.transaction.Transaction;
import dev.mmussatto.expensetracker.entities.vendor.onlinestore.OnlineStore;
import dev.mmussatto.expensetracker.entities.vendor.physicalstore.PhysicalStore;
import dev.mmussatto.expensetracker.exceptions.IncorrectVendorTypeException;
import dev.mmussatto.expensetracker.exceptions.ResourceAlreadyExistsException;
import dev.mmussatto.expensetracker.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendorServiceImplTest {

    // -------------- Constants ----------------------------
    public static final Integer ID = 1;
    public static final String NAME = "Test";
    public static final String URL = "www.test.com";
    public static final String ADDRESS = "Test St. 123";
    public static final Transaction TRANSACTION = new Transaction();
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 1;


    @Mock
    VendorRepository<Vendor> vendorRepository;

    @InjectMocks
    VendorServiceImpl vendorService;



    @BeforeEach
    void setUp() {
        TRANSACTION.setId(1);
    }


    // -------------- READ ----------------------------
    @Test
    void getAllVendors() {
        OnlineStore onlineStore = new OnlineStore();
        onlineStore.setId(1);

        PhysicalStore physicalStore = new PhysicalStore();
        physicalStore.setId(2);

        List<Vendor> vendors = Arrays.asList(onlineStore, physicalStore);

        when(vendorRepository.findAll()).thenReturn(vendors);

        List<Vendor> returnedList = vendorService.getAllVendors();

        assertEquals(vendors.size(), returnedList.size());
    }

    @Test
    void getVendorById() {

        OnlineStore onlineStore = createOnlineStore();

        when(vendorRepository.findById(onlineStore.getId())).thenReturn(Optional.of(onlineStore));

        Vendor returnedEntity = vendorService.getVendorById(onlineStore.getId());

        assertEquals(onlineStore.getId(), returnedEntity.getId());
        assertEquals(onlineStore.getName(), returnedEntity.getName());
        assertEquals(onlineStore.getUrl(), ((OnlineStore) returnedEntity).getUrl());
        assertEquals(onlineStore.getTransactions(), returnedEntity.getTransactions());
    }

    @Test
    void getVendorById_NotFound() {

        Integer notFoundId = 123;

        when(vendorRepository.findById(notFoundId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vendorService.getVendorById(notFoundId));
    }

    @Test
    void getVendorByName() {

        PhysicalStore physicalStore = createPhysicalStore();

        when(vendorRepository.findByName(physicalStore.getName())).thenReturn(Optional.of(physicalStore));

        Vendor returnedEntity = vendorService.getVendorByName(physicalStore.getName());

        assertEquals(physicalStore.getId(), returnedEntity.getId());
        assertEquals(physicalStore.getName(), returnedEntity.getName());
        assertEquals(physicalStore.getAddress(), ((PhysicalStore) returnedEntity).getAddress());
        assertEquals(physicalStore.getTransactions(), returnedEntity.getTransactions());
    }

    @Test
    void getVendorByName_NotFound() {

        when(vendorRepository.findByName(NAME)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vendorService.getVendorByName(NAME));
    }


    // -------------- CREATE ----------------------------
    @Test
    void createNewVendor() {

        OnlineStore passedEntity = new OnlineStore(NAME, URL);

        OnlineStore savedEntity = new OnlineStore(passedEntity.getName(), passedEntity.getUrl());
        savedEntity.setId(ID);


        when(vendorRepository.findByName(passedEntity.getName())).thenReturn(Optional.empty());
        when(vendorRepository.save(passedEntity)).thenReturn(savedEntity);

        Vendor returnedVendor = vendorService.createNewVendor(passedEntity);

        assertEquals(savedEntity.getId(), returnedVendor.getId());
        assertEquals(passedEntity.getName(), returnedVendor.getName());
        assertEquals(passedEntity.getUrl(), ((OnlineStore) returnedVendor).getUrl());
    }

    @Test
    void createNewVendor_NameAlreadyExists() {

        OnlineStore passedEntity = new OnlineStore(NAME, URL);

        OnlineStore savedEntity = createOnlineStore();

        when(vendorRepository.findByName(passedEntity.getName())).thenReturn(Optional.of(savedEntity));


        assertThrows(ResourceAlreadyExistsException.class, () -> vendorService.createNewVendor(passedEntity));
    }

    @Test
    void createNewVendor_UrlAlreadyExists() {

        OnlineStore passedEntity = new OnlineStore(NAME, URL);

        OnlineStore savedEntity = createOnlineStore();


        when(vendorRepository.findByName(passedEntity.getName())).thenReturn(Optional.empty());
        when(vendorRepository.findByUrl(passedEntity.getUrl())).thenReturn(Optional.of(savedEntity));


        assertThrows(ResourceAlreadyExistsException.class, () -> vendorService.createNewVendor(passedEntity));
    }

    @Test
    void createNewVendor_AddressAlreadyExists() {

        PhysicalStore passedEntity = new PhysicalStore(NAME, ADDRESS);

        PhysicalStore savedEntity = createPhysicalStore();


        when(vendorRepository.findByName(passedEntity.getName())).thenReturn(Optional.empty());
        when(vendorRepository.findByAddress(passedEntity.getAddress())).thenReturn(Optional.of(savedEntity));


        assertThrows(ResourceAlreadyExistsException.class, () -> vendorService.createNewVendor(passedEntity));
    }


    // -------------- UPDATE ----------------------------
    @Test
    void updateVendorById() {

        PhysicalStore passedEntity = new PhysicalStore("Test Update", "New Address");

        PhysicalStore originalEntity = createPhysicalStore();

        PhysicalStore toUpdateEntity = new PhysicalStore(passedEntity.getName(), passedEntity.getAddress());
        toUpdateEntity.setId(originalEntity.getId());

        PhysicalStore updatedEntity = new PhysicalStore(toUpdateEntity.getName(), toUpdateEntity.getAddress());
        updatedEntity.setId(toUpdateEntity.getId());
        updatedEntity.setTransactions(originalEntity.getTransactions());

        when(vendorRepository.findById(originalEntity.getId())).thenReturn(Optional.of(originalEntity));
        when(vendorRepository.findByName(passedEntity.getName())).thenReturn(Optional.empty());
        when(vendorRepository.findByAddress(passedEntity.getAddress())).thenReturn(Optional.empty());
        when(vendorRepository.save(toUpdateEntity)).thenReturn(updatedEntity);

        Vendor returnedVendor = vendorService.updateVendorById(originalEntity.getId(), passedEntity);

        assertEquals(originalEntity.getId(), returnedVendor.getId());
        assertEquals(passedEntity.getName(), returnedVendor.getName());
        assertEquals(passedEntity.getAddress(), ((PhysicalStore) returnedVendor).getAddress());
        assertEquals(originalEntity.getTransactions(), returnedVendor.getTransactions());

        verify(vendorRepository, times(1)).findByName(passedEntity.getName());
        verify(vendorRepository, times(1)).save(toUpdateEntity);

    }

    @Test
    void updateVendorById_NotFound() {

        PhysicalStore passedEntity = new PhysicalStore("Test Update", "New Address");

        PhysicalStore originalEntity = createPhysicalStore();

        when(vendorRepository.findById(originalEntity.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> vendorService.updateVendorById(originalEntity.getId(), passedEntity));
    }

    @Test
    void updateVendorById_NameAlreadyExists() {

        PhysicalStore passedEntity = new PhysicalStore("Test Update", "New Address");

        PhysicalStore originalEntity = createPhysicalStore();

        PhysicalStore anotherSavedEntity = new PhysicalStore(passedEntity.getName(), ADDRESS);

        when(vendorRepository.findById(originalEntity.getId())).thenReturn(Optional.of(originalEntity));
        when(vendorRepository.findByName(passedEntity.getName())).thenReturn(Optional.of(anotherSavedEntity));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> vendorService.updateVendorById(originalEntity.getId(), passedEntity));

    }

    @Test
    void  updateVendorById_UrlAlreadyExists() {

        OnlineStore passedEntity = new OnlineStore("Test Update", "www.newUrl.com");

        OnlineStore originalEntity = createOnlineStore();

        OnlineStore anotherSavedEntity = new OnlineStore(NAME, passedEntity.getUrl());


        when(vendorRepository.findById(originalEntity.getId())).thenReturn(Optional.of(originalEntity));
        when(vendorRepository.findByName(passedEntity.getName())).thenReturn(Optional.empty());
        when(vendorRepository.findByUrl(passedEntity.getUrl())).thenReturn(Optional.of(anotherSavedEntity));


        assertThrows(ResourceAlreadyExistsException.class,
                () -> vendorService.updateVendorById(originalEntity.getId(), passedEntity));
    }

    @Test
    void updateVendorById_AddressAlreadyExists() {

        PhysicalStore passedEntity = new PhysicalStore("Test Update", "New Address");

        PhysicalStore originalEntity = createPhysicalStore();

        PhysicalStore anotherSavedEntity = new PhysicalStore(NAME, passedEntity.getAddress());

        when(vendorRepository.findById(originalEntity.getId())).thenReturn(Optional.of(originalEntity));
        when(vendorRepository.findByName(passedEntity.getName())).thenReturn(Optional.empty());
        when(vendorRepository.findByAddress(passedEntity.getAddress())).thenReturn(Optional.of(anotherSavedEntity));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> vendorService.updateVendorById(originalEntity.getId(), passedEntity));

    }

    @Test
    void updateVendorById_IncorrectType() {

        PhysicalStore passedEntity = new PhysicalStore("Test Update", "New Address");

        OnlineStore originalEntity = createOnlineStore();


        when(vendorRepository.findById(originalEntity.getId())).thenReturn(Optional.of(originalEntity));
        when(vendorRepository.findByName(passedEntity.getName())).thenReturn(Optional.empty());

        assertThrows(IncorrectVendorTypeException.class,
                () -> vendorService.updateVendorById(originalEntity.getId(), passedEntity));

    }


    // -------------- PATCH ----------------------------
    @Test
    void patchVendorById() {

        PhysicalStore passedEntity = new PhysicalStore("Test Patch", "New Address");

        PhysicalStore originalEntity = createPhysicalStore();

        PhysicalStore updatedEntity = new PhysicalStore(passedEntity.getName(), passedEntity.getAddress());
        updatedEntity.setId(originalEntity.getId());
        updatedEntity.setTransactions(originalEntity.getTransactions());

        when(vendorRepository.findById(originalEntity.getId())).thenReturn(Optional.of(originalEntity));
        when(vendorRepository.findByName(passedEntity.getName())).thenReturn(Optional.empty());
        when(vendorRepository.save((updatedEntity))).thenReturn(updatedEntity);

        Vendor returnedVendor = vendorService.patchVendorById(originalEntity.getId(), passedEntity);

        assertEquals(originalEntity.getId(), returnedVendor.getId());
        assertEquals(passedEntity.getName(), returnedVendor.getName());
        assertEquals(passedEntity.getAddress(), ((PhysicalStore) returnedVendor).getAddress());
        assertEquals(originalEntity.getTransactions(), returnedVendor.getTransactions());

        verify(vendorRepository, times(1)).findByName(passedEntity.getName());
        verify(vendorRepository, times(1)).save(updatedEntity);
    }

    @Test
    void patchVendorById_UpdateOnlyName() {

        PhysicalStore passedEntity = new PhysicalStore();
        passedEntity.setName("Test Patch");
        //missing address

        PhysicalStore originalEntity = createPhysicalStore();

        PhysicalStore updatedEntity = new PhysicalStore(passedEntity.getName(), originalEntity.getAddress());
        updatedEntity.setId(originalEntity.getId());
        updatedEntity.setTransactions(originalEntity.getTransactions());

        when(vendorRepository.findById(originalEntity.getId())).thenReturn(Optional.of(originalEntity));
        when(vendorRepository.findByName(passedEntity.getName())).thenReturn(Optional.empty());
        when(vendorRepository.save((updatedEntity))).thenReturn(updatedEntity);

        Vendor returnedVendor = vendorService.patchVendorById(originalEntity.getId(), passedEntity);

        assertEquals(originalEntity.getId(), returnedVendor.getId());
        assertEquals(passedEntity.getName(), returnedVendor.getName());
        assertEquals(originalEntity.getAddress(), ((PhysicalStore) returnedVendor).getAddress());
        assertEquals(originalEntity.getTransactions(), returnedVendor.getTransactions());

        verify(vendorRepository, times(1)).findByName(passedEntity.getName());
        verify(vendorRepository, times(1)).save(updatedEntity);
    }

    @Test
    void patchVendorById_UpdateOnlyAddress() {
        PhysicalStore passedEntity = new PhysicalStore();
        //missing name
        passedEntity.setAddress("New Address");

        PhysicalStore originalEntity = createPhysicalStore();

        PhysicalStore updatedEntity = new PhysicalStore(originalEntity.getName(), passedEntity.getAddress());
        updatedEntity.setId(originalEntity.getId());
        updatedEntity.setTransactions(originalEntity.getTransactions());

        when(vendorRepository.findById(originalEntity.getId())).thenReturn(Optional.of(originalEntity));
        when(vendorRepository.findByAddress(passedEntity.getAddress())).thenReturn(Optional.empty());
        when(vendorRepository.save((updatedEntity))).thenReturn(updatedEntity);

        Vendor returnedVendor = vendorService.patchVendorById(originalEntity.getId(), passedEntity);

        assertEquals(originalEntity.getId(), returnedVendor.getId());
        assertEquals(originalEntity.getName(), returnedVendor.getName());
        assertEquals(passedEntity.getAddress(), ((PhysicalStore) returnedVendor).getAddress());
        assertEquals(originalEntity.getTransactions(), returnedVendor.getTransactions());

        verify(vendorRepository, times(1)).findByAddress(passedEntity.getAddress());
        verify(vendorRepository, times(1)).save(updatedEntity);
    }

    @Test
    void patchVendorById_UpdateOnlyUrl() {

        OnlineStore passedEntity = new OnlineStore();
        //missing name
        passedEntity.setUrl("www.newurl.com");

        OnlineStore originalEntity = createOnlineStore();

        OnlineStore updatedEntity = new OnlineStore(originalEntity.getName(), passedEntity.getUrl());
        updatedEntity.setId(originalEntity.getId());
        updatedEntity.setTransactions(originalEntity.getTransactions());

        when(vendorRepository.findById(originalEntity.getId())).thenReturn(Optional.of(originalEntity));
        when(vendorRepository.findByUrl(passedEntity.getUrl())).thenReturn(Optional.empty());
        when(vendorRepository.save((updatedEntity))).thenReturn(updatedEntity);

        Vendor returnedVendor = vendorService.patchVendorById(originalEntity.getId(), passedEntity);

        assertEquals(originalEntity.getId(), returnedVendor.getId());
        assertEquals(originalEntity.getName(), returnedVendor.getName());
        assertEquals(passedEntity.getUrl(), ((OnlineStore) returnedVendor).getUrl());
        assertEquals(originalEntity.getTransactions(), returnedVendor.getTransactions());

        verify(vendorRepository, times(1)).findByUrl(passedEntity.getUrl());
        verify(vendorRepository, times(1)).save(updatedEntity);
    }

    @Test
    void patchVendorById_NotFound() {

        PhysicalStore passedEntity = new PhysicalStore("Test Patch", "New Address");

        PhysicalStore originalEntity = createPhysicalStore();

        when(vendorRepository.findById(originalEntity.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> vendorService.patchVendorById(originalEntity.getId(), passedEntity));
    }

    @Test
    void patchVendorById_NameAlreadyExists() {

        PhysicalStore passedEntity = new PhysicalStore("Test Patch", "New Address");

        PhysicalStore originalEntity = createPhysicalStore();

        PhysicalStore anotherSavedEntity = new PhysicalStore(passedEntity.getName(), ADDRESS);

        when(vendorRepository.findById(originalEntity.getId())).thenReturn(Optional.of(originalEntity));
        when(vendorRepository.findByName(passedEntity.getName())).thenReturn(Optional.of(anotherSavedEntity));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> vendorService.patchVendorById(originalEntity.getId(), passedEntity));
    }

    @Test
    void  patchVendorById_UrlAlreadyExists() {

        OnlineStore passedEntity = new OnlineStore("Test Update", "www.newUrl.com");

        OnlineStore originalEntity = createOnlineStore();

        OnlineStore anotherSavedEntity = new OnlineStore(NAME, passedEntity.getUrl());


        when(vendorRepository.findById(originalEntity.getId())).thenReturn(Optional.of(originalEntity));
        when(vendorRepository.findByName(passedEntity.getName())).thenReturn(Optional.empty());
        when(vendorRepository.findByUrl(passedEntity.getUrl())).thenReturn(Optional.of(anotherSavedEntity));


        assertThrows(ResourceAlreadyExistsException.class,
                () -> vendorService.patchVendorById(originalEntity.getId(), passedEntity));
    }

    @Test
    void patchVendorById_AddressAlreadyExists() {

        PhysicalStore passedEntity = new PhysicalStore("Test Update", "New Address");

        PhysicalStore originalEntity = createPhysicalStore();

        PhysicalStore anotherSavedEntity = new PhysicalStore(NAME, passedEntity.getAddress());

        when(vendorRepository.findById(originalEntity.getId())).thenReturn(Optional.of(originalEntity));
        when(vendorRepository.findByName(passedEntity.getName())).thenReturn(Optional.empty());
        when(vendorRepository.findByAddress(passedEntity.getAddress())).thenReturn(Optional.of(anotherSavedEntity));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> vendorService.patchVendorById(originalEntity.getId(), passedEntity));

    }


    // -------------- DELETE ----------------------------
    @Test
    void deleteVendorById() {

        when(vendorRepository.findById(ID)).thenReturn(Optional.of(new PhysicalStore()));
        doNothing().when(vendorRepository).deleteById(ID);

        vendorService.deleteVendorById(ID);

        verify(vendorRepository, times(1)).deleteById(ID);

    }

    @Test
    void deleteVendorById_NotFound() {

        when(vendorRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> vendorService.deleteVendorById(ID));
    }


    // -------------- TRANSACTIONS ----------------------------
    @Test
    void getTransactionsByVendorId() {

        Transaction t1 = new Transaction();
        t1.setId(1);
        t1.setAmount(53.00);
        t1.setDescription("Test Transaction 1");

        Transaction t2 = new Transaction();
        t2.setId(2);
        t2.setAmount(123.00);
        t2.setDescription("Test Transaction 2");

        List<Transaction> transactions = Arrays.asList(t1, t2);

        PhysicalStore physicalStore = new PhysicalStore(NAME, ADDRESS);
        physicalStore.setId(ID);
        t1.setVendor(physicalStore);
        t2.setVendor(physicalStore);
        physicalStore.setTransactions(transactions);

        //Create page returned by the service
        Pageable pageable = PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE, Sort.by("date"));
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), transactions.size());

        Page<Transaction> pagedTransactions = new PageImpl<Transaction>(
                transactions.subList(start, end), pageable, transactions.size());

        when(vendorRepository.findById(physicalStore.getId())).thenReturn(Optional.of(physicalStore));

        Page<Transaction> returnPagedTransactions = vendorService.getTransactionsByVendorId(physicalStore.getId(), DEFAULT_PAGE, DEFAULT_SIZE);

        assertEquals(DEFAULT_SIZE, returnPagedTransactions.getContent().size(), "Wrong number of transactions");
        assertEquals(pagedTransactions, returnPagedTransactions);
    }

    @Test
    void getTransactionsByVendorId_NotFound() {

        Integer notFoundId = 123;

        when(vendorRepository.findById(notFoundId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                vendorService.getTransactionsByVendorId(notFoundId, DEFAULT_PAGE, DEFAULT_SIZE));
    }


    // -------------- Helpers ----------------------------
    private OnlineStore createOnlineStore() {
        OnlineStore onlineStore = new OnlineStore(NAME, URL);
        onlineStore.setId(ID);
        onlineStore.getTransactions().add(TRANSACTION);
        return onlineStore;
    }

    private PhysicalStore createPhysicalStore() {
        PhysicalStore physicalStore = new PhysicalStore(NAME, ADDRESS);
        physicalStore.setId(ID);
        physicalStore.getTransactions().add(TRANSACTION);
        return physicalStore;
    }
}