/*
 * Created by murilo.mussatto on 15/03/2023
 */

package dev.mmussatto.expensetracker.services;

import dev.mmussatto.expensetracker.domain.Transaction;
import dev.mmussatto.expensetracker.domain.Vendor;
import dev.mmussatto.expensetracker.repositories.VendorRepository;
import dev.mmussatto.expensetracker.services.exceptions.ResourceAlreadyExistsException;
import dev.mmussatto.expensetracker.services.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class VendorServiceImpl<V extends Vendor> implements VendorService<V> {

    VendorRepository<V> vendorRepository;

    public VendorServiceImpl(VendorRepository<V> vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @Override
    public List<V> getAllVendors() {
        return vendorRepository.findAll();
    }

    @Override
    public V getVendorById(Integer id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Vendor %d not found!", id)));
    }

    @Override
    public V getVendorByName(String name) {
        return vendorRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Vendor %s not found!", name)));
    }

    @Override
    public V createNewVendor(V vendor) {
        checkIfNameIsAlreadyInUse(vendor);

        return vendorRepository.save(vendor);
    }

    @Override
    public V updateVendorById(Integer id, V vendor) {
        return null;
    }

    @Override
    public V patchVendorById(Integer id, V vendor) {
        return null;
    }

    @Override
    public void deleteVendorById(Integer id) {

    }

    @Override
    public Set<Transaction> getTransactionsById(Integer id) {
        return null;
    }



    private void checkIfNameIsAlreadyInUse(V vendor) {
        vendorRepository.findByName(vendor.getName()).ifPresent(savedVendor -> {
            throw new ResourceAlreadyExistsException(String.format("Vendor %s already exists", vendor.getName()),
                    "/api/tags/" + savedVendor.getId());
        });
    }
}
