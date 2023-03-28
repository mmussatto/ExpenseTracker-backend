/*
 * Created by murilo.mussatto on 21/03/2023
 */

package dev.mmussatto.expensetracker.controllers;

import dev.mmussatto.expensetracker.api.mappers.TransactionMapper;
import dev.mmussatto.expensetracker.api.model.ListDTO;
import dev.mmussatto.expensetracker.api.model.TransactionDTO;
import dev.mmussatto.expensetracker.domain.Transaction;
import dev.mmussatto.expensetracker.services.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ListDTO<TransactionDTO> getAllTransactions () {
        return new ListDTO<>(transactionService.getAllTransactions()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TransactionDTO getTransactionById (@PathVariable final Integer id) {
        return convertToDTO(transactionService.getTransactionById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(TransactionDTO.allFieldsValidation.class)
    public TransactionDTO createNewTransaction (@Valid @RequestBody TransactionDTO transactionDTO) {
        Transaction entity = convertToEntity(transactionDTO);
        return convertToDTO(transactionService.createNewTransaction(entity));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Validated(TransactionDTO.allFieldsValidation.class)
    public TransactionDTO updateTransactionById (@PathVariable final Integer id, @Valid @RequestBody TransactionDTO transactionDTO) {
        Transaction entity = convertToEntity(transactionDTO);
        return convertToDTO(transactionService.updateTransactionById(id, entity));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Validated(TransactionDTO.onlyIdValidation.class)
    public TransactionDTO patchTransactionById (@PathVariable final Integer id, @Valid @RequestBody TransactionDTO transactionDTO) {
        Transaction entity = convertToEntity(transactionDTO);
        return convertToDTO(transactionService.patchTransactionById(id, entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransactionById (@PathVariable final Integer id) {
        transactionService.deleteTransactionById(id);
    }


    private TransactionDTO convertToDTO (Transaction entity) {
        TransactionDTO dto = transactionMapper.convertToDTO(entity);
        dto.setPath("/api/transactions/" + dto.getId());
        return dto;
    }

    private Transaction convertToEntity (TransactionDTO dto) {
        return transactionMapper.convertToEntity(dto);
    }
}
