/*
 * Created by murilo.mussatto on 07/03/2023
 */

package dev.mmussatto.expensetracker.entities.paymentmethod;

import dev.mmussatto.expensetracker.entities.helpers.ListDTO;
import dev.mmussatto.expensetracker.entities.helpers.PageDTO;
import dev.mmussatto.expensetracker.entities.transaction.Transaction;
import dev.mmussatto.expensetracker.entities.transaction.TransactionDTO;
import dev.mmussatto.expensetracker.entities.transaction.TransactionMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Payment Methods", description = "CRUD API for Payment Method entity")
@Validated
@RestController
@RequestMapping("/api/payment-methods")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;
    private final PaymentMethodMapper paymentMethodMapper;

    public PaymentMethodController(PaymentMethodService paymentMethodService, PaymentMethodMapper paymentMethodMapper) {
        this.paymentMethodService = paymentMethodService;
        this.paymentMethodMapper = paymentMethodMapper;
    }


    @Operation(summary = "Get all payment methods")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the payment methods", useReturnTypeSchema = true)
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ListDTO<PaymentMethodDTO> getAllPaymentMethods () {
        List<PaymentMethodDTO> list = paymentMethodService.getAllPaymentMethods()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new ListDTO<>(list);
    }


    @Operation(summary = "Get a payment method by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the payment method", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Payment method not found", content = @Content)
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentMethodDTO getPaymentMethodById (@PathVariable final Integer id) {
        return convertToDTO(paymentMethodService.getPaymentMethodById(id));
    }


    @Operation(summary = "Get a payment method by its name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the payment method", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Payment method not found", content = @Content)
    })
    @GetMapping("/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentMethodDTO getPaymentMethodByName (@PathVariable final String name) {
        return convertToDTO(paymentMethodService.getPaymentMethodByName(name));
    }


    @Operation(summary = "Create payment method")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment method created", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Invalid body supplied", content = @Content),
            @ApiResponse(responseCode = "409", description = "Name supplied is already in use", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(PaymentMethodDTO.allFieldsValidation.class)
    public PaymentMethodDTO createNewPaymentMethod (@Valid @RequestBody PaymentMethodDTO paymentMethodDTO) {
        PaymentMethod entity = convertToEntity(paymentMethodDTO);
        return convertToDTO(paymentMethodService.createNewPaymentMethod(entity));
    }


    @Operation(summary = "Update payment method")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment method updated", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Invalid body supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Payment method not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Name supplied is already in use", content = @Content)
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Validated(PaymentMethodDTO.allFieldsValidation.class)
    public PaymentMethodDTO updatePaymentMethodById (@PathVariable final Integer id,
                                                     @Valid @RequestBody PaymentMethodDTO paymentMethodDTO) {
        PaymentMethod entity = convertToEntity(paymentMethodDTO);
        return convertToDTO(paymentMethodService.updatePaymentMethodById(id, entity));
    }


    @Operation(summary = "Patch payment method")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment method patched", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Invalid body supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Payment method not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Name supplied is already in use", content = @Content)
    })
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Validated(PaymentMethodDTO.onlyIdValidation.class)
    public PaymentMethodDTO patchPaymentMethodById (@PathVariable final Integer id,
                                                    @Valid @RequestBody PaymentMethodDTO paymentMethodDTO) {
        PaymentMethod entity = convertToEntity(paymentMethodDTO);
        return convertToDTO(paymentMethodService.patchPaymentMethodById(id, entity));
    }


    @Operation(summary = "Delete payment method")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Payment method deleted", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Payment method not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePaymentMethodById (@PathVariable final Integer id) {
        paymentMethodService.deletePaymentMethodById(id);
    }


    @Operation(summary = "Get all transactions from a payment method")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the payment method's transactions", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Payment method not found", content = @Content)
    })
    @GetMapping("/{id}/transactions")
    @ResponseStatus(HttpStatus.OK)
    public PageDTO<TransactionDTO> getPaymentMethodTransactionsById (@PathVariable final Integer id,
                                                                     @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                                     @RequestParam(value = "size", defaultValue = "1", required = false) int size) {

        Page<Transaction> paginatedTransactions = paymentMethodService.getPaymentMethodTransactionsById(id, page, size);

        PageDTO<TransactionDTO> returnPage = new PageDTO<>();

        returnPage.setContent(paginatedTransactions.getContent()
                .stream()
                .map(transaction -> {
                    TransactionDTO dto = TransactionMapper.INSTANCE.convertToDTO(transaction);
                    dto.setPath("/api/transactions/" + dto.getId());
                    return dto;
                })
                .collect(Collectors.toList()));

        returnPage.setPageNo(paginatedTransactions.getNumber());
        returnPage.setPageSize(paginatedTransactions.getSize());
        returnPage.setTotalElements(paginatedTransactions.getTotalElements());
        returnPage.setTotalPages(paginatedTransactions.getTotalPages());

        if (paginatedTransactions.hasNext())
            returnPage.setNextPage(String.format("/api/payment-methods/%d/transactions?page=%d&size=%d",
                    id, paginatedTransactions.getNumber()+1, paginatedTransactions.getSize()));

        if (paginatedTransactions.hasPrevious())
            returnPage.setPreviousPage(String.format("/api/payment-methods/%d/transactions?page=%d&size=%d",
                    id, paginatedTransactions.getNumber()-1, paginatedTransactions.getSize()));

        return returnPage;
    }


    //Helpers
    private PaymentMethodDTO convertToDTO (PaymentMethod entity) {
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.convertToDTO(entity);
        paymentMethodDTO.setPath("/api/payment-methods/" + paymentMethodDTO.getId());
        return paymentMethodDTO;
    }

    private PaymentMethod convertToEntity(PaymentMethodDTO paymentMethodDTO) {
        return paymentMethodMapper.convertToEntity(paymentMethodDTO);
    }
}
