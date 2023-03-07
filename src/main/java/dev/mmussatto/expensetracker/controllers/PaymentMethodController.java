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
        return null;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentMethodDTO getPaymentMethodById (@PathVariable final Integer id) {
        return null;
    }

    @GetMapping("/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentMethodDTO getPaymentMethodByName (@PathVariable final String name) {
        return null;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentMethodDTO createNewPaymentMethod (@Valid @RequestBody PaymentMethodDTO paymentMethodDTO) {
        return  null;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentMethodDTO updatePaymentMethodById (@PathVariable final Integer id,
                                                     @Valid @RequestBody PaymentMethodDTO paymentMethodDTO) {
        return null;
    }

    @PutMapping("/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentMethodDTO updatePaymentMethodByName (@PathVariable final String name,
                                                     @Valid @RequestBody PaymentMethodDTO paymentMethodDTO) {
        return null;
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentMethodDTO patchPaymentMethodById (@PathVariable final Integer id,
                                                     @Valid @RequestBody PaymentMethodDTO paymentMethodDTO) {
        return null;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePaymentMethodById (@PathVariable final Integer id) {

    }

    @DeleteMapping("/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePaymentMethodById (@PathVariable final String name) {

    }

}
