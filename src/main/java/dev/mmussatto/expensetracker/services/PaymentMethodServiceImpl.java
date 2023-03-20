/*
 * Created by murilo.mussatto on 03/03/2023
 */

package dev.mmussatto.expensetracker.services;

import dev.mmussatto.expensetracker.domain.PaymentMethod;
import dev.mmussatto.expensetracker.domain.Transaction;
import dev.mmussatto.expensetracker.repositories.PaymentMethodRepository;
import dev.mmussatto.expensetracker.services.exceptions.ResourceAlreadyExistsException;
import dev.mmussatto.expensetracker.services.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    public PaymentMethodServiceImpl(PaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Override
    public List<PaymentMethod> getAllPaymentMethods() {
        return paymentMethodRepository.findAll();
    }

    @Override
    public PaymentMethod getPaymentMethodById(Integer id) {
        return paymentMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment Method " + id + " not found!"));
    }

    @Override
    public PaymentMethod getPaymentMethodByName(String name) {
        return paymentMethodRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Payment Method " + name + " not found!"));
    }

    @Override
    public PaymentMethod createNewPaymentMethod(PaymentMethod paymentMethod) {

        checkIfNameIsAlreadyInUse(paymentMethod);

        return paymentMethodRepository.save(paymentMethod);
    }

    @Override
    public PaymentMethod updatePaymentMethodById(Integer id, PaymentMethod paymentMethod) {

        paymentMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment Method " + id + " not found!"));

        checkIfNameIsAlreadyInUse(paymentMethod);

        paymentMethod.setId(id);

        return paymentMethodRepository.save(paymentMethod);
    }

    @Override
    public PaymentMethod patchPaymentMethodById(Integer id, PaymentMethod paymentMethod) {
        return paymentMethodRepository.findById(id).map(savedEntity -> {

            if (paymentMethod.getName() != null) {

                checkIfNameIsAlreadyInUse(paymentMethod);

                savedEntity.setName(paymentMethod.getName());
            }

            if (paymentMethod.getType() != null)
                savedEntity.setType(paymentMethod.getType());

            if (paymentMethod.getTransactions() != null && paymentMethod.getTransactions().size() != 0)
                savedEntity.setTransactions(paymentMethod.getTransactions());

            return paymentMethodRepository.save(savedEntity);

        }).orElseThrow(() -> new ResourceNotFoundException("Payment Method " + id + " not found!"));
    }

    @Override
    public void deletePaymentMethodById(Integer id) {

        paymentMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment Method " + id + " not found!"));

        paymentMethodRepository.deleteById(id);
    }

    @Override
    public Set<Transaction> getPaymentMethodTransactionsById(Integer id) {

        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment Method " + id + " not found!"));

        return paymentMethod.getTransactions();
    }


    private void checkIfNameIsAlreadyInUse(PaymentMethod paymentMethodDTO) {
        paymentMethodRepository.findByName(paymentMethodDTO.getName()).ifPresent(paymentMethod -> {
            throw new ResourceAlreadyExistsException("Payment Method " + paymentMethodDTO.getName() + " already exists.",
                    "/api/payment-methods/" + paymentMethod.getId());
        });
    }
}
