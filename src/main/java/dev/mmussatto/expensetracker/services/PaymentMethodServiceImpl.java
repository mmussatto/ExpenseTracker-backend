/*
 * Created by murilo.mussatto on 03/03/2023
 */

package dev.mmussatto.expensetracker.services;

import dev.mmussatto.expensetracker.api.mappers.PaymentMethodMapper;
import dev.mmussatto.expensetracker.api.model.PaymentMethodDTO;
import dev.mmussatto.expensetracker.repositories.PaymentMethodRepository;

import java.util.List;

public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodMapper paymentMethodMapper;
    private final PaymentMethodRepository paymentMethodRepository;

    public PaymentMethodServiceImpl(PaymentMethodMapper paymentMethodMapper,
                                    PaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodMapper = paymentMethodMapper;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Override
    public List<PaymentMethodDTO> getAllPaymentMethods() {
        return null;
    }

    @Override
    public PaymentMethodDTO getPaymentMethodById(Integer id) {
        return null;
    }

    @Override
    public PaymentMethodDTO getPaymentMethodByName(String name) {
        return null;
    }

    @Override
    public PaymentMethodDTO createNewPaymentMethod(PaymentMethodDTO paymentMethodDTO) {
        return null;
    }

    @Override
    public PaymentMethodDTO updatePaymentMethodById(Integer id, PaymentMethodDTO paymentMethodDTO) {
        return null;
    }

    @Override
    public PaymentMethodDTO updatePaymentMethodByName(String name, PaymentMethodDTO paymentMethodDTO) {
        return null;
    }

    @Override
    public PaymentMethodDTO patchPaymentMethodById(Integer id, PaymentMethodDTO paymentMethodDTO) {
        return null;
    }

    @Override
    public void deletePaymentMethodById(Integer id) {

    }

    @Override
    public void deletePaymentMethodByName(String name) {

    }
}
