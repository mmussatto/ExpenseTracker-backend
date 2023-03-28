/*
 * Created by murilo.mussatto on 15/03/2023
 */

package dev.mmussatto.expensetracker.vendor;

import dev.mmussatto.expensetracker.entities.transaction.Transaction;
import dev.mmussatto.expensetracker.entities.vendor.Vendor;
import dev.mmussatto.expensetracker.entities.vendor.VendorRepository;
import dev.mmussatto.expensetracker.entities.vendor.VendorService;
import dev.mmussatto.expensetracker.entities.vendor.VendorServiceImpl;
import dev.mmussatto.expensetracker.entities.vendor.onlinestore.OnlineStore;
import dev.mmussatto.expensetracker.entities.vendor.physicalstore.PhysicalStore;
import dev.mmussatto.expensetracker.exceptions.ResourceAlreadyExistsException;
import dev.mmussatto.expensetracker.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendorServiceImplTest {

    @Mock
    VendorRepository<Vendor> vendorRepository;

    VendorService<Vendor> vendorService;

    public static final Integer ID = 1;
    public static final String NAME = "Test";
    public static final String URL = "www.test.com";
    public static final String ADDRESS = "Test St. 123";
    public static final Transaction TRANSACTION = new Transaction();

    @BeforeEach
    void setUp() {
        vendorService = new VendorServiceImpl<>(vendorRepository);
        TRANSACTION.setId(1);
    }

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

        String notFoundName = "asdf";

        when(vendorRepository.findByName(notFoundName)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vendorService.getVendorByName(notFoundName));
    }

    @Test
    void createNewVendor() {

        OnlineStore passedVendor = new OnlineStore(NAME, URL);
        passedVendor.getTransactions().add(TRANSACTION);

        OnlineStore savedVendor = createOnlineStore();


        when(vendorRepository.findByName(passedVendor.getName())).thenReturn(Optional.empty());
        when(vendorRepository.save(passedVendor)).thenReturn(savedVendor);

        Vendor returnedVendor = vendorService.createNewVendor(passedVendor);

        assertEquals(savedVendor.getId(), returnedVendor.getId());
        assertEquals(passedVendor.getName(), returnedVendor.getName());
        assertEquals(passedVendor.getUrl(), ((OnlineStore) returnedVendor).getUrl());
        assertEquals(passedVendor.getTransactions(), returnedVendor.getTransactions());
    }

    @Test
    void createNewVendor_NameAlreadyExists() {

        OnlineStore passedVendor = new OnlineStore(NAME, URL);
        passedVendor.getTransactions().add(TRANSACTION);

        OnlineStore savedVendor = createOnlineStore();


        when(vendorRepository.findByName(passedVendor.getName())).thenReturn(Optional.of(savedVendor));


        assertThrows(ResourceAlreadyExistsException.class, () -> vendorService.createNewVendor(passedVendor));
    }

    @Test
    void createNewVendor_UrlAlreadyExists() {

        OnlineStore passedVendor = new OnlineStore(NAME, URL);
        passedVendor.getTransactions().add(TRANSACTION);

        OnlineStore savedVendor = createOnlineStore();


        when(vendorRepository.findByName(passedVendor.getName())).thenReturn(Optional.empty());
        when(vendorRepository.findByUrl(passedVendor.getUrl())).thenReturn(Optional.of(savedVendor));


        assertThrows(ResourceAlreadyExistsException.class, () -> vendorService.createNewVendor(passedVendor));
    }

    @Test
    void createNewVendor_AddressAlreadyExists() {

        PhysicalStore passedVendor = new PhysicalStore(NAME, ADDRESS);
        passedVendor.getTransactions().add(TRANSACTION);

        PhysicalStore savedVendor = createPhysicalStore();


        when(vendorRepository.findByName(passedVendor.getName())).thenReturn(Optional.empty());
        when(vendorRepository.findByAddress(passedVendor.getAddress())).thenReturn(Optional.of(savedVendor));


        assertThrows(ResourceAlreadyExistsException.class, () -> vendorService.createNewVendor(passedVendor));
    }

    @Test
    void updateVendorById() {

        PhysicalStore passedVendor = new PhysicalStore("Test Update", "New Address");

        PhysicalStore originalVendor = createPhysicalStore();

        PhysicalStore updatedVendor = new PhysicalStore(passedVendor.getName(), passedVendor.getAddress());
        updatedVendor.setId(originalVendor.getId());
        updatedVendor.setTransactions(passedVendor.getTransactions());

        when(vendorRepository.findById(originalVendor.getId())).thenReturn(Optional.of(originalVendor));
        when(vendorRepository.findByName(passedVendor.getName())).thenReturn(Optional.empty());
        when(vendorRepository.save(updatedVendor)).thenReturn(updatedVendor);

        Vendor returnedVendor = vendorService.updateVendorById(originalVendor.getId(), passedVendor);

        assertEquals(originalVendor.getId(), returnedVendor.getId());
        assertEquals(passedVendor.getName(), returnedVendor.getName());
        assertEquals(passedVendor.getAddress(), ((PhysicalStore) returnedVendor).getAddress());
        assertEquals(passedVendor.getTransactions(), returnedVendor.getTransactions());

    }

    @Test
    void updateVendorById_NotFound() {

        PhysicalStore passedVendor = new PhysicalStore("Test Update", "New Address");

        PhysicalStore originalVendor = createPhysicalStore();

        when(vendorRepository.findById(originalVendor.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> vendorService.updateVendorById(originalVendor.getId(), passedVendor));
    }

    @Test
    void updateVendorById_NameAlreadyExists() {

        PhysicalStore passedVendor = new PhysicalStore("Test Update", "New Address");

        PhysicalStore originalVendor = createPhysicalStore();

        PhysicalStore anotherSavedVendor = new PhysicalStore(passedVendor.getName(), ADDRESS);

        when(vendorRepository.findById(originalVendor.getId())).thenReturn(Optional.of(originalVendor));
        when(vendorRepository.findByName(passedVendor.getName())).thenReturn(Optional.of(anotherSavedVendor));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> vendorService.updateVendorById(originalVendor.getId(), passedVendor));

    }

    @Test
    void patchVendorById() {
        PhysicalStore passedVendor = new PhysicalStore("Test Patch", "New Address");

        PhysicalStore originalVendor = createPhysicalStore();

        PhysicalStore toUpdateVendor = createPhysicalStore();

        PhysicalStore updatedVendor = new PhysicalStore(passedVendor.getName(), passedVendor.getAddress());
        updatedVendor.setId(originalVendor.getId());
        updatedVendor.setTransactions(passedVendor.getTransactions());

        when(vendorRepository.findById(originalVendor.getId())).thenReturn(Optional.of(toUpdateVendor));
        when(vendorRepository.findByName(passedVendor.getName())).thenReturn(Optional.empty());
        when(vendorRepository.save((updatedVendor))).thenReturn(updatedVendor);

        Vendor returnedVendor = vendorService.patchVendorById(originalVendor.getId(), passedVendor);

        assertEquals(originalVendor.getId(), returnedVendor.getId());
        assertEquals(passedVendor.getName(), returnedVendor.getName());
        assertEquals(passedVendor.getAddress(), ((PhysicalStore) returnedVendor).getAddress());
        assertEquals(passedVendor.getTransactions(), returnedVendor.getTransactions());
    }

    @Test
    void patchVendorById_UpdateOnlyName() {
        PhysicalStore passedVendor = new PhysicalStore();
        passedVendor.setName("Test Patch");

        PhysicalStore originalVendor = createPhysicalStore();

        PhysicalStore toUpdateVendor = createPhysicalStore();

        PhysicalStore updatedVendor = new PhysicalStore(passedVendor.getName(), originalVendor.getAddress());
        updatedVendor.setId(originalVendor.getId());
        updatedVendor.setTransactions(originalVendor.getTransactions());

        when(vendorRepository.findById(originalVendor.getId())).thenReturn(Optional.of(toUpdateVendor));
        when(vendorRepository.findByName(passedVendor.getName())).thenReturn(Optional.empty());
        when(vendorRepository.save((updatedVendor))).thenReturn(updatedVendor);

        Vendor returnedVendor = vendorService.patchVendorById(originalVendor.getId(), passedVendor);

        assertEquals(originalVendor.getId(), returnedVendor.getId());
        assertEquals(passedVendor.getName(), returnedVendor.getName());
        assertEquals(originalVendor.getAddress(), ((PhysicalStore) returnedVendor).getAddress());
        assertEquals(originalVendor.getTransactions(), returnedVendor.getTransactions());
    }

    @Test
    void patchVendorById_UpdateOnlyAddress() {
        PhysicalStore passedVendor = new PhysicalStore();
        passedVendor.setAddress("New Address");

        PhysicalStore originalVendor = createPhysicalStore();

        PhysicalStore toUpdateVendor = createPhysicalStore();

        PhysicalStore updatedVendor = new PhysicalStore(originalVendor.getName(), passedVendor.getAddress());
        updatedVendor.setId(originalVendor.getId());
        updatedVendor.setTransactions(originalVendor.getTransactions());

        when(vendorRepository.findById(originalVendor.getId())).thenReturn(Optional.of(toUpdateVendor));
        when(vendorRepository.save((updatedVendor))).thenReturn(updatedVendor);

        Vendor returnedVendor = vendorService.patchVendorById(originalVendor.getId(), passedVendor);

        assertEquals(originalVendor.getId(), returnedVendor.getId());
        assertEquals(originalVendor.getName(), returnedVendor.getName());
        assertEquals(passedVendor.getAddress(), ((PhysicalStore) returnedVendor).getAddress());
        assertEquals(originalVendor.getTransactions(), returnedVendor.getTransactions());
    }

    @Test
    void patchVendorById_UpdateOnlyUrl() {
        OnlineStore passedVendor = new OnlineStore();
        passedVendor.setUrl("www.newurl.com");

        OnlineStore originalVendor = createOnlineStore();

        OnlineStore toUpdateVendor = createOnlineStore();

        OnlineStore updatedVendor = new OnlineStore(originalVendor.getName(), passedVendor.getUrl());
        updatedVendor.setId(originalVendor.getId());
        updatedVendor.setTransactions(originalVendor.getTransactions());

        when(vendorRepository.findById(originalVendor.getId())).thenReturn(Optional.of(toUpdateVendor));
        when(vendorRepository.save((updatedVendor))).thenReturn(updatedVendor);

        Vendor returnedVendor = vendorService.patchVendorById(originalVendor.getId(), passedVendor);

        assertEquals(originalVendor.getId(), returnedVendor.getId());
        assertEquals(originalVendor.getName(), returnedVendor.getName());
        assertEquals(passedVendor.getUrl(), ((OnlineStore) returnedVendor).getUrl());
        assertEquals(originalVendor.getTransactions(), returnedVendor.getTransactions());
    }

    @Test
    void patchVendorById_NotFound() {
        PhysicalStore passedVendor = new PhysicalStore("Test Patch", "New Address");

        PhysicalStore originalVendor = createPhysicalStore();

        when(vendorRepository.findById(originalVendor.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> vendorService.patchVendorById(originalVendor.getId(), passedVendor));
    }

    @Test
    void patchVendorById_NameAlreadyExists() {
        PhysicalStore passedVendor = new PhysicalStore("Test Patch", "New Address");

        PhysicalStore originalVendor = createPhysicalStore();

        PhysicalStore anotherSavedVendor = new PhysicalStore(passedVendor.getName(), ADDRESS);

        when(vendorRepository.findById(originalVendor.getId())).thenReturn(Optional.of(originalVendor));
        when(vendorRepository.findByName(passedVendor.getName())).thenReturn(Optional.of(anotherSavedVendor));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> vendorService.patchVendorById(originalVendor.getId(), passedVendor));
    }

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


    @Test
    void getTransactionsById() {

        Transaction t1 = new Transaction();
        t1.setId(1);
        t1.setAmount(53.00);
        t1.setDescription("Test Transaction 1");

        Transaction t2 = new Transaction();
        t2.setId(2);
        t2.setAmount(123.00);
        t2.setDescription("Test Transaction 2");

        Set<Transaction> transactions = new HashSet<>(Arrays.asList(t1, t2));

        PhysicalStore physicalStore = new PhysicalStore(NAME, ADDRESS);
        physicalStore.setId(ID);
        t1.setVendor(physicalStore);
        t2.setVendor(physicalStore);
        physicalStore.setTransactions(transactions);

        when(vendorRepository.findById(physicalStore.getId())).thenReturn(Optional.of(physicalStore));

        Set<Transaction> returnedSet = vendorService.getTransactionsById(physicalStore.getId());

        assertEquals(transactions, returnedSet);
    }

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