/*
 * Created by murilo.mussatto on 15/03/2023
 */

package dev.mmussatto.expensetracker.entities.vendor;

import dev.mmussatto.expensetracker.entities.helpers.ListDTO;
import dev.mmussatto.expensetracker.entities.transaction.Transaction;
import dev.mmussatto.expensetracker.entities.transaction.TransactionDTO;
import dev.mmussatto.expensetracker.entities.transaction.TransactionMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Tag(name = "Vendors", description = "CRUD API for Vendor entity")
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


    @Operation(summary = "Get all vendors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the vendors", useReturnTypeSchema = true)
    })
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


    @Operation(summary = "Get a vendor by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the vendor", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Vendor not found", content = @Content)
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VendorDTO getVendorById (@PathVariable final Integer id) {
        return getVendorDTO(vendorService.getVendorById(id));
    }


    @Operation(summary = "Get a vendor by its name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the vendor", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Invalid name supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Vendor not found", content = @Content)
    })
    @GetMapping("/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public VendorDTO getVendorByName (@PathVariable final String name) {
        return getVendorDTO(vendorService.getVendorByName(name));
    }


    @Operation(summary = "Create vendor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vendor Created", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Invalid body supplied", content = @Content),
            @ApiResponse(responseCode = "409", description = "Name, address or url supplied is already in use", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(VendorDTO.allFieldsValidation.class)
    public VendorDTO createNewVendor (@Valid @RequestBody VendorDTO vendorDTO) {

        Vendor vendor = getVendor(vendorDTO);

        vendor = vendorService.createNewVendor(vendor);

        return getVendorDTO(vendor);
    }


    @Operation(summary = "Update vendor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vendor updated", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Invalid body supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Vendor not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Name, address or url supplied is already in use", content = @Content)
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Validated(VendorDTO.allFieldsValidation.class)
    public VendorDTO updateVendorById (@PathVariable final Integer id, @Valid @RequestBody VendorDTO vendorDTO) {

        Vendor vendor = getVendor(vendorDTO);

        vendor = vendorService.updateVendorById(id, vendor);

        return getVendorDTO(vendor);
    }


    @Operation(summary = "Patch vendor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vendor patched", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Invalid body supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Vendor not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Name, address or url supplied is already in use", content = @Content)
    })
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Validated(VendorDTO.onlyIdValidation.class)
    public VendorDTO patchVendorById (@PathVariable final Integer id, @Valid @RequestBody VendorDTO vendorDTO) {

        Vendor vendor = getVendor(vendorDTO);

        vendor = vendorService.patchVendorById(id, vendor);

        return getVendorDTO(vendor);
    }


    @Operation(summary = "Delete vendor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Vendor deleted", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Vendor not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVendorById (@PathVariable final Integer id) {
        vendorService.deleteVendorById(id);
    }



    //Transactions
    @Operation(summary = "Get all transactions from a vendor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the vendor's transactions", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Vendor not found", content = @Content)
    })
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



    //Mappers
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
