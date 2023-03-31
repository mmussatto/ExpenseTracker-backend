/*
 * Created by murilo.mussatto on 03/03/2023
 */

package dev.mmussatto.expensetracker.entities.paymentmethod;

import dev.mmussatto.expensetracker.entities.transaction.Transaction;
import dev.mmussatto.expensetracker.exceptions.ResourceAlreadyExistsException;
import dev.mmussatto.expensetracker.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public Page<Transaction> getPaymentMethodTransactionsById(Integer id, int page, int size) {

        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment Method " + id + " not found!"));

        List<Transaction>  transactions = paymentMethod.getTransactions();

        Pageable pageable = PageRequest.of(page, size, Sort.by("date"));
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), transactions.size());

        return new PageImpl<Transaction>(transactions.subList(start, end), pageable, transactions.size());
    }


    private void checkIfNameIsAlreadyInUse(PaymentMethod paymentMethodDTO) {
        paymentMethodRepository.findByName(paymentMethodDTO.getName()).ifPresent(paymentMethod -> {
            throw new ResourceAlreadyExistsException("Payment Method " + paymentMethodDTO.getName() + " already exists.",
                    "/api/payment-methods/" + paymentMethod.getId());
        });
    }
}
