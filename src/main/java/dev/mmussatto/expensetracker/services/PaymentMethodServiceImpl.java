/*
 * Created by murilo.mussatto on 03/03/2023
 */

package dev.mmussatto.expensetracker.services;

import dev.mmussatto.expensetracker.api.mappers.PaymentMethodMapper;
import dev.mmussatto.expensetracker.api.model.PaymentMethodDTO;
import dev.mmussatto.expensetracker.domain.PaymentMethod;
import dev.mmussatto.expensetracker.repositories.PaymentMethodRepository;
import dev.mmussatto.expensetracker.services.exceptions.ResourceAlreadyExistsException;
import dev.mmussatto.expensetracker.services.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
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
        return paymentMethodRepository.findAll()
                .stream()
                .map(paymentMethod -> {
                    PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.convertToDTO(paymentMethod);
                    paymentMethodDTO.setPath("/api/payment-methods/" + paymentMethodDTO.getId());
                    return paymentMethodDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public PaymentMethodDTO getPaymentMethodById(Integer id) {
        return paymentMethodRepository.findById(id)
                .map(paymentMethod -> {
                    PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.convertToDTO(paymentMethod);
                    paymentMethodDTO.setPath("/api/payment-methods/" + paymentMethodDTO.getId());
                    return paymentMethodDTO;
                })
                .orElseThrow(() -> new ResourceNotFoundException("Payment Method " + id + " not found!"));
    }

    @Override
    public PaymentMethodDTO getPaymentMethodByName(String name) {
        return paymentMethodRepository.findByName(name)
                .map(paymentMethod -> {
                    PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.convertToDTO(paymentMethod);
                    paymentMethodDTO.setPath("/api/payment-methods/" + paymentMethodDTO.getId());
                    return paymentMethodDTO;
                })
                .orElseThrow(() -> new ResourceNotFoundException("Payment Method " + name + " not found!"));
    }

    @Override
    public PaymentMethodDTO createNewPaymentMethod(PaymentMethodDTO paymentMethodDTO) {

        checkIfNameIsAlreadyInUse(paymentMethodDTO);

        return saveAndReturn(paymentMethodMapper.convertToEntity(paymentMethodDTO));
    }

    @Override
    public PaymentMethodDTO updatePaymentMethodById(Integer id, PaymentMethodDTO paymentMethodDTO) {

        paymentMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment Method " + id + " not found!"));

        checkIfNameIsAlreadyInUse(paymentMethodDTO);

        PaymentMethod paymentMethod = paymentMethodMapper.convertToEntity(paymentMethodDTO);
        paymentMethod.setId(id);

        return saveAndReturn(paymentMethod);
    }

    @Override
    public PaymentMethodDTO patchPaymentMethodById(Integer id, PaymentMethodDTO paymentMethodDTO) {
        return paymentMethodRepository.findById(id).map(paymentMethod -> {

            if (paymentMethodDTO.getName() != null) {

                checkIfNameIsAlreadyInUse(paymentMethodDTO);

                paymentMethod.setName(paymentMethodDTO.getName());
            }

            if (paymentMethodDTO.getType() != null)
                paymentMethod.setType(paymentMethodDTO.getType());

            if (paymentMethodDTO.getTransactions() != null && paymentMethodDTO.getTransactions().size() != 0)
                paymentMethod.setTransactions(paymentMethodDTO.getTransactions());

            return saveAndReturn(paymentMethod);

        }).orElseThrow(() -> new ResourceNotFoundException("Payment Method " + id + " not found!"));
    }

    @Override
    public void deletePaymentMethodById(Integer id) {

        paymentMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment Method " + id + " not found!"));

        paymentMethodRepository.deleteById(id);
    }


    private PaymentMethodDTO saveAndReturn(PaymentMethod paymentMethod) {
        PaymentMethod savedPaymentMethod = paymentMethodRepository.save(paymentMethod);

        PaymentMethodDTO returnDTO = paymentMethodMapper.convertToDTO(savedPaymentMethod);
        returnDTO.setPath("/api/payment-methods/" + returnDTO.getId());

        return returnDTO;
    }

    private void checkIfNameIsAlreadyInUse(PaymentMethodDTO paymentMethodDTO) {
        paymentMethodRepository.findByName(paymentMethodDTO.getName()).ifPresent(paymentMethod -> {
            throw new ResourceAlreadyExistsException("Payment Method " + paymentMethodDTO.getName() + " already exists.",
                    "/api/payment-methods/" + paymentMethod.getId());
        });
    }
}
