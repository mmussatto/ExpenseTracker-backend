/*
 * Created by murilo.mussatto on 15/03/2023
 */

package dev.mmussatto.expensetracker.entities.vendor;

import dev.mmussatto.expensetracker.entities.helpers.ListDTO;
import dev.mmussatto.expensetracker.entities.transaction.Transaction;
import dev.mmussatto.expensetracker.entities.transaction.TransactionDTO;
import dev.mmussatto.expensetracker.entities.transaction.TransactionMapper;
import dev.mmussatto.expensetracker.entities.vendor.onlinestore.OnlineStore;
import dev.mmussatto.expensetracker.entities.vendor.onlinestore.OnlineStoreDTO;
import dev.mmussatto.expensetracker.entities.vendor.onlinestore.OnlineStoreMapper;
import dev.mmussatto.expensetracker.entities.vendor.physicalstore.PhysicalStore;
import dev.mmussatto.expensetracker.entities.vendor.physicalstore.PhysicalStoreDTO;
import dev.mmussatto.expensetracker.entities.vendor.physicalstore.PhysicalStoreMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private final VendorService<Vendor> vendorService;

    public VendorController(VendorService<Vendor> vendorService) {
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
    public VendorDTO getVendorByName (@PathVariable final String name) {
        return getVendorDTO(vendorService.getVendorByName(name));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(VendorDTO.allFieldsValidation.class)
    public VendorDTO createNewVendor (@Valid @RequestBody VendorDTO vendorDTO) {

        Vendor vendor = getVendor(vendorDTO);

        vendor = vendorService.createNewVendor(vendor);

        return getVendorDTO(vendor);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Validated(VendorDTO.allFieldsValidation.class)
    public VendorDTO updateVendorById (@PathVariable final Integer id, @Valid @RequestBody VendorDTO vendorDTO) {

        Vendor vendor = getVendor(vendorDTO);

        vendor = vendorService.updateVendorById(id, vendor);

        return getVendorDTO(vendor);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Validated(VendorDTO.onlyIdValidation.class)
    public VendorDTO patchVendorById (@PathVariable final Integer id, @Valid @RequestBody VendorDTO vendorDTO) {

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
