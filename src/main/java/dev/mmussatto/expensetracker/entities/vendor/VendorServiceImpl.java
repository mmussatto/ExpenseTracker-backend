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
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VendorServiceImpl implements VendorService {

    VendorRepository<Vendor> vendorRepository;

    public VendorServiceImpl(VendorRepository<Vendor> vendorRepository) {
        this.vendorRepository = vendorRepository;
    }


    @Override
    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    @Override
    public Vendor getVendorById(Integer id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Vendor '%d' not found!", id)));
    }

    @Override
    public Vendor getVendorByName(String name) {
        return vendorRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Vendor '%s' not found!", name)));
    }

    @Override
    public Vendor createNewVendor(Vendor vendor) {

        checkIfNameIsAlreadyInUse(vendor);

        if (vendor instanceof OnlineStore)
            checkIfUrlIsAlreadyInUse((OnlineStore) vendor);
        else if (vendor instanceof PhysicalStore)
            checkIfAddressIsAlreadyInUse((PhysicalStore) vendor);


        return vendorRepository.save(vendor);
    }

    @Override
    public Vendor updateVendorById(Integer id, Vendor vendor) {

        Vendor savedVendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Vendor '%d' not found!", id)));

        checkIfNameIsAlreadyInUse(vendor);

        if (vendor instanceof OnlineStore)
            checkIfUrlIsAlreadyInUse((OnlineStore) vendor);
        else if (vendor instanceof PhysicalStore)
            checkIfAddressIsAlreadyInUse((PhysicalStore) vendor);

        vendor.setId(id);

        if(vendor.getClass() != savedVendor.getClass())
            throw new IncorrectVendorTypeException(
                    String.format("Incorrect type for vendor '%d'. Change type in request body or create new vendor", id));

        return vendorRepository.save(vendor);
    }

    @Override
    public Vendor patchVendorById(Integer id, Vendor vendor) {
        return vendorRepository.findById(id).map(savedVendor -> {

            if(vendor.getClass() != savedVendor.getClass())
                throw new IncorrectVendorTypeException(
                        String.format("Incorrect type for vendor '%d'. Change type in request body or create new vendor", id));

            if (vendor.getName() != null) {
                checkIfNameIsAlreadyInUse(vendor);
                savedVendor.setName(vendor.getName());
            }

            if (vendor instanceof PhysicalStore
                    && ((PhysicalStore) vendor).getAddress() != null
                    && savedVendor instanceof PhysicalStore) {

                checkIfAddressIsAlreadyInUse((PhysicalStore) vendor);

                ((PhysicalStore) savedVendor).setAddress(((PhysicalStore) vendor).getAddress());
            }

            if (vendor instanceof OnlineStore
                    && ((OnlineStore) vendor).getUrl() != null
                    && savedVendor instanceof OnlineStore) {

                checkIfUrlIsAlreadyInUse((OnlineStore) vendor);

                ((OnlineStore) savedVendor).setUrl(((OnlineStore) vendor).getUrl());
            }

            return vendorRepository.save(savedVendor);
        }).orElseThrow(() -> new ResourceNotFoundException(String.format("Vendor '%d' not found!", id)));
    }

    @Override
    public void deleteVendorById(Integer id) {

        vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Vendor '%d' not found!", id)));

        vendorRepository.deleteById(id);
    }

    @Override
    public Page<Transaction> getTransactionsByVendorId(Integer id, int page, int size) {
        Vendor savedVendor =  vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Vendor '%d' not found!", id)));

        List<Transaction>  transactions = savedVendor.getTransactions();

        Pageable pageable = PageRequest.of(page, size, Sort.by("date"));
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), transactions.size());

        return new PageImpl<Transaction>(transactions.subList(start, end), pageable, transactions.size());
    }



    private void checkIfAddressIsAlreadyInUse(PhysicalStore vendor) {
        vendorRepository.findByAddress(vendor.getAddress()).ifPresent(savedVendor -> {
            throw new ResourceAlreadyExistsException(String.format("Address '%s' already exists", vendor.getAddress()),
                    "/api/vendors/" + savedVendor.getId());
        });
    }

    private void checkIfUrlIsAlreadyInUse(OnlineStore vendor) {
        vendorRepository.findByUrl(vendor.getUrl()).ifPresent(savedVendor -> {
            throw new ResourceAlreadyExistsException(String.format("Url '%s' already exists", vendor.getUrl()),
                    "/api/vendors/" + savedVendor.getId());
        });
    }

    private void checkIfNameIsAlreadyInUse(Vendor vendor) {
        vendorRepository.findByName(vendor.getName()).ifPresent(savedVendor -> {
            throw new ResourceAlreadyExistsException(String.format("Vendor '%s' already exists", vendor.getName()),
                    "/api/vendors/" + savedVendor.getId());
        });
    }
}
