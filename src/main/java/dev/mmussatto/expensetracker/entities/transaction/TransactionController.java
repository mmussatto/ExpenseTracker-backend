/*
 * Created by murilo.mussatto on 21/03/2023
 */

package dev.mmussatto.expensetracker.entities.transaction;

import dev.mmussatto.expensetracker.entities.helpers.PageDTO;
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

import java.util.stream.Collectors;

@Tag(name = "Transactions", description = "CRUD API for Transaction entity")
@RestController
@Validated
@RequestMapping ("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    public TransactionController(TransactionService transactionService, TransactionMapper transactionMapper) {
        this.transactionService = transactionService;
        this.transactionMapper = transactionMapper;
    }


    @Operation(summary = "Get all transactions with paging")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the transactions page", useReturnTypeSchema = true)
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageDTO<TransactionDTO> getPaginatedTransactions (@RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                             @RequestParam(value = "size", defaultValue = "1", required = false) int size) {

        Page<Transaction> paginatedTransactions = transactionService.getPaginated(page, size);

        PageDTO<TransactionDTO> returnPage = new PageDTO<>();

        returnPage.setContent(paginatedTransactions.getContent()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));

        returnPage.setPageNo(paginatedTransactions.getNumber());
        returnPage.setPageSize(paginatedTransactions.getSize());
        returnPage.setTotalElements(paginatedTransactions.getTotalElements());
        returnPage.setTotalPages(paginatedTransactions.getTotalPages());

        if (paginatedTransactions.hasNext())
            returnPage.setNextPage(String.format("/api/transactions?page=%d&size=%d",
                    paginatedTransactions.getNumber()+1, paginatedTransactions.getSize()));

        if (paginatedTransactions.hasPrevious())
            returnPage.setPreviousPage(String.format("/api/transactions?page=%d&size=%d",
                    paginatedTransactions.getNumber()-1, paginatedTransactions.getSize()));

        return returnPage;
    }


    @Operation(summary = "Get a transaction by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the transaction", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content)
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TransactionDTO getTransactionById (@PathVariable final Integer id) {
        return convertToDTO(transactionService.getTransactionById(id));
    }


    @Operation(summary = "Create transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transaction created", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Invalid body supplied", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(RequestTransactionDTO.allFieldsValidation.class)
    public TransactionDTO createNewTransaction (@Valid @RequestBody RequestTransactionDTO transactionDTO) {
        Transaction entity = transactionMapper.convertRequestToEntity(transactionDTO);
        return convertToDTO(transactionService.createNewTransaction(entity));
    }


    @Operation(summary = "Update transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction updated", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Invalid body supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content)
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Validated(RequestTransactionDTO.allFieldsValidation.class)
    public TransactionDTO updateTransactionById (@PathVariable final Integer id, @Valid @RequestBody RequestTransactionDTO transactionDTO) {
        Transaction entity = transactionMapper.convertRequestToEntity(transactionDTO);
        return convertToDTO(transactionService.updateTransactionById(id, entity));
    }


    @Operation(summary = "Patch transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction patched", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Invalid body supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content)
    })
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Validated(RequestTransactionDTO.onlyIdValidation.class)
    public TransactionDTO patchTransactionById (@PathVariable final Integer id, @Valid @RequestBody RequestTransactionDTO transactionDTO) {
        Transaction entity = transactionMapper.convertRequestToEntity(transactionDTO);
        return convertToDTO(transactionService.patchTransactionById(id, entity));
    }


    @Operation(summary = "Delete transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Transaction deleted", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransactionById (@PathVariable final Integer id) {
        transactionService.deleteTransactionById(id);
    }




    @GetMapping("/month/{month}")
    @ResponseStatus(HttpStatus.OK)
    public PageDTO<TransactionDTO> getTransactionsByMonth (@PathVariable final String month,
                                                           @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                           @RequestParam(value = "size", defaultValue = "10", required = false) int size) {


        Page<Transaction> paginatedTransactions = transactionService.getTransactionsByMonth(page, size, month);

        PageDTO<TransactionDTO> returnPage = new PageDTO<>();

        returnPage.setContent(paginatedTransactions.getContent()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));

        returnPage.setPageNo(paginatedTransactions.getNumber());
        returnPage.setPageSize(paginatedTransactions.getSize());
        returnPage.setTotalElements(paginatedTransactions.getTotalElements());
        returnPage.setTotalPages(paginatedTransactions.getTotalPages());

        if (paginatedTransactions.hasNext())
            returnPage.setNextPage(String.format("/api/transactions/month/%s?page=%d&size=%d",
                    month, paginatedTransactions.getNumber()+1, paginatedTransactions.getSize()));

        if (paginatedTransactions.hasPrevious())
            returnPage.setPreviousPage(String.format("/api/transactions/month/%s?page=%d&size=%d",
                    month, paginatedTransactions.getNumber()-1, paginatedTransactions.getSize()));

        return returnPage;
    }


    //Mappers
    private TransactionDTO convertToDTO (Transaction entity) {
        TransactionDTO dto = transactionMapper.convertToDTO(entity);
        dto.setPath("/api/transactions/" + dto.getId());
        return dto;
    }

    private Transaction convertToEntity (TransactionDTO dto) {
        return transactionMapper.convertToEntity(dto);
    }
}
