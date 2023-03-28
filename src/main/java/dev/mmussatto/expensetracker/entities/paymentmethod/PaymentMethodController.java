/*
 * Created by murilo.mussatto on 07/03/2023
 */

package dev.mmussatto.expensetracker.entities.paymentmethod;

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
@RequestMapping("/api/payment-methods")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;
    private final PaymentMethodMapper paymentMethodMapper;

    public PaymentMethodController(PaymentMethodService paymentMethodService, PaymentMethodMapper paymentMethodMapper) {
        this.paymentMethodService = paymentMethodService;
        this.paymentMethodMapper = paymentMethodMapper;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ListDTO<PaymentMethodDTO> getAllPaymentMethods () {
        List<PaymentMethodDTO> list = paymentMethodService.getAllPaymentMethods()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new ListDTO<>(list);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentMethodDTO getPaymentMethodById (@PathVariable final Integer id) {
        return convertToDTO(paymentMethodService.getPaymentMethodById(id));
    }

    @GetMapping("/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentMethodDTO getPaymentMethodByName (@PathVariable final String name) {
        return convertToDTO(paymentMethodService.getPaymentMethodByName(name));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(PaymentMethodDTO.allFieldsValidation.class)
    public PaymentMethodDTO createNewPaymentMethod (@Valid @RequestBody PaymentMethodDTO paymentMethodDTO) {
        PaymentMethod entity = convertToEntity(paymentMethodDTO);
        return convertToDTO(paymentMethodService.createNewPaymentMethod(entity));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Validated(PaymentMethodDTO.allFieldsValidation.class)
    public PaymentMethodDTO updatePaymentMethodById (@PathVariable final Integer id,
                                                     @Valid @RequestBody PaymentMethodDTO paymentMethodDTO) {
        PaymentMethod entity = convertToEntity(paymentMethodDTO);
        return convertToDTO(paymentMethodService.updatePaymentMethodById(id, entity));
    }


    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Validated(PaymentMethodDTO.onlyIdValidation.class)
    public PaymentMethodDTO patchPaymentMethodById (@PathVariable final Integer id,
                                                    @Valid @RequestBody PaymentMethodDTO paymentMethodDTO) {
        PaymentMethod entity = convertToEntity(paymentMethodDTO);
        return convertToDTO(paymentMethodService.patchPaymentMethodById(id, entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePaymentMethodById (@PathVariable final Integer id) {
        paymentMethodService.deletePaymentMethodById(id);
    }

    @GetMapping("/{id}/transactions")
    @ResponseStatus(HttpStatus.OK)
    public ListDTO<TransactionDTO> getPaymentMethodTransactionsById (@PathVariable final Integer id) {
        Set<Transaction> transactions = paymentMethodService.getPaymentMethodTransactionsById(id);

        //Convert to dto
        return new ListDTO<>(transactions.stream()
                .map(transaction -> {
                    TransactionDTO transactionDTO = TransactionMapper.INSTANCE.convertToDTO(transaction);
                    transactionDTO.setPath("/api/transactions/" + transactionDTO.getId());
                    return transactionDTO;
                }).collect(Collectors.toList()));
    }


    private PaymentMethodDTO convertToDTO (PaymentMethod entity) {
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.convertToDTO(entity);
        paymentMethodDTO.setPath("/api/payment-methods/" + paymentMethodDTO.getId());
        return paymentMethodDTO;
    }

    private PaymentMethod convertToEntity(PaymentMethodDTO paymentMethodDTO) {
        return paymentMethodMapper.convertToEntity(paymentMethodDTO);
    }
}
