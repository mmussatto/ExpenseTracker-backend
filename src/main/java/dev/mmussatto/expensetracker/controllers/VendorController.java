/*
 * Created by murilo.mussatto on 15/03/2023
 */

package dev.mmussatto.expensetracker.controllers;

import dev.mmussatto.expensetracker.api.mappers.OnlineStoreMapper;
import dev.mmussatto.expensetracker.api.mappers.PhysicalStoreMapper;
import dev.mmussatto.expensetracker.api.mappers.TransactionMapper;
import dev.mmussatto.expensetracker.api.model.*;
import dev.mmussatto.expensetracker.domain.OnlineStore;
import dev.mmussatto.expensetracker.domain.PhysicalStore;
import dev.mmussatto.expensetracker.domain.Transaction;
import dev.mmussatto.expensetracker.domain.Vendor;
import dev.mmussatto.expensetracker.services.VendorServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private final VendorServiceImpl<Vendor> vendorService;

    public VendorController(VendorServiceImpl<Vendor> vendorService) {
        this.vendorService = vendorService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ListDTO<VendorDTO> getAllVendors () {
        //Convert List to DTO
        List<VendorDTO>  list = vendorService.getAllVendors()
                .stream()
                .map(this::getVendorDTO)
                .collect(Collectors.toList());

        return new ListDTO<>(list);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VendorDTO getVendorById (@PathVariable final Integer id) {
        return getVendorDTO(vendorService.getVendorById(id));
    }

    @GetMapping("/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public VendorDTO getVendorById (@PathVariable final String name) {
        return getVendorDTO(vendorService.getVendorByName(name));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VendorDTO createNewVendor (@RequestBody VendorDTO vendorDTO) {

        Vendor vendor = getVendor(vendorDTO);

        vendor = vendorService.createNewVendor(vendor);

        return getVendorDTO(vendor);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VendorDTO updateVendorById (@PathVariable final Integer id, @RequestBody VendorDTO vendorDTO) {

        Vendor vendor = getVendor(vendorDTO);

        vendor = vendorService.updateVendorById(id, vendor);

        return getVendorDTO(vendor);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VendorDTO patchVendorById (@PathVariable final Integer id, @RequestBody VendorDTO vendorDTO) {

        Vendor vendor = getVendor(vendorDTO);

        vendor = vendorService.patchVendorById(id, vendor);

        return getVendorDTO(vendor);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVendorById (@PathVariable final Integer id) {
        vendorService.deleteVendorById(id);
    }

    @GetMapping("/{id}/transactions")
    @ResponseStatus(HttpStatus.OK)
    public ListDTO<TransactionDTO> getVendorTransactionsById (@PathVariable final Integer id) {
        Set<Transaction> transactions = vendorService.getTransactionsById(id);

        return new ListDTO<>(transactions.stream()
                .map(transaction -> {
                    TransactionDTO transactionDTO = TransactionMapper.INSTANCE.convertToDTO(transaction);
                    transactionDTO.setPath("/api/transactions/" + transactionDTO.getId());
                    return transactionDTO;
                }).collect(Collectors.toList()));
    }




    private VendorDTO getVendorDTO(Vendor vendor) {
        VendorDTO vendorDTO = null;

        //Convert using the correct mapper
        if (vendor instanceof OnlineStore) {
            vendorDTO = OnlineStoreMapper.INSTANCE.convertToDTO((OnlineStore) vendor);
        } else if (vendor instanceof PhysicalStore) {
            vendorDTO = PhysicalStoreMapper.INSTANCE.convertToDTO((PhysicalStore) vendor);
        }

        //Set Path
        if (vendorDTO != null) {
            vendorDTO.setPath("/api/vendors/" + vendorDTO.getId());
        }

        return vendorDTO;
    }

    private Vendor getVendor(VendorDTO vendorDTO) {
        Vendor vendor = null;

        //Convert using the correct mapper
        if (vendorDTO instanceof OnlineStoreDTO) {
            vendor = OnlineStoreMapper.INSTANCE.convertToEntity((OnlineStoreDTO) vendorDTO);
        } else if (vendorDTO instanceof PhysicalStoreDTO) {
            vendor = PhysicalStoreMapper.INSTANCE.convertToEntity((PhysicalStoreDTO) vendorDTO);
        }

        return vendor;
    }
}