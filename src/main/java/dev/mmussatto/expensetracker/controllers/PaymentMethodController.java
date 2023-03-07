/*
 * Created by murilo.mussatto on 07/03/2023
 */

package dev.mmussatto.expensetracker.controllers;

import dev.mmussatto.expensetracker.api.model.ListDTO;
import dev.mmussatto.expensetracker.api.model.PaymentMethodDTO;
import dev.mmussatto.expensetracker.services.PaymentMethodService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment-methods")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    public PaymentMethodController(PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ListDTO<PaymentMethodDTO> getAllPaymentMethods () {
        return new ListDTO<>(paymentMethodService.getAllPaymentMethods());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentMethodDTO getPaymentMethodById (@PathVariable final Integer id) {
        return paymentMethodService.getPaymentMethodById(id);
    }

    @GetMapping("/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentMethodDTO getPaymentMethodByName (@PathVariable final String name) {
        return paymentMethodService.getPaymentMethodByName(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentMethodDTO createNewPaymentMethod (@Valid @RequestBody PaymentMethodDTO paymentMethodDTO) {
        return paymentMethodService.createNewPaymentMethod(paymentMethodDTO);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentMethodDTO updatePaymentMethodById (@PathVariable final Integer id,
                                                     @Valid @RequestBody PaymentMethodDTO paymentMethodDTO) {
        return paymentMethodService.updatePaymentMethodById(id, paymentMethodDTO);
    }

    @PutMapping("/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentMethodDTO updatePaymentMethodByName (@PathVariable final String name,
                                                     @Valid @RequestBody PaymentMethodDTO paymentMethodDTO) {
        return paymentMethodService.updatePaymentMethodByName(name, paymentMethodDTO);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentMethodDTO patchPaymentMethodById (@PathVariable final Integer id,
                                                     @Valid @RequestBody PaymentMethodDTO paymentMethodDTO) {
        return paymentMethodService.patchPaymentMethodById(id, paymentMethodDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePaymentMethodById (@PathVariable final Integer id) {
        paymentMethodService.deletePaymentMethodById(id);
    }

    @DeleteMapping("/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePaymentMethodById (@PathVariable final String name) {
        paymentMethodService.deletePaymentMethodByName(name);
    }

}
