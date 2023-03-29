/*
 * Created by murilo.mussatto on 15/03/2023
 */

package dev.mmussatto.expensetracker.entities.vendor;

import dev.mmussatto.expensetracker.entities.helpers.ListDTO;
import dev.mmussatto.expensetracker.entities.transaction.Transaction;
import dev.mmussatto.expensetracker.entities.transaction.TransactionDTO;
import dev.mmussatto.expensetracker.entities.transaction.TransactionMapper;
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

    private final VendorMapper vendorMapper;

    public VendorController(VendorService<Vendor> vendorService, VendorMapper vendorMapper) {
        this.vendorService = vendorService;
        this.vendorMapper = vendorMapper;
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
        VendorDTO vendorDTO = vendorMapper.convertToDTO(vendor);

        //Set Path
        if (vendorDTO != null) {
            vendorDTO.setPath("/api/vendors/" + vendorDTO.getId());
        }

        return vendorDTO;
    }

    private Vendor getVendor(VendorDTO vendorDTO) {
        return vendorMapper.convertToEntity(vendorDTO);
    }
}
