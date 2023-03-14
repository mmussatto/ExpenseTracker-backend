/*
 * Created by murilo.mussatto on 03/03/2023
 */

package dev.mmussatto.expensetracker.services;

import dev.mmussatto.expensetracker.api.model.PaymentMethodDTO;
import dev.mmussatto.expensetracker.domain.Transaction;

import java.util.List;
import java.util.Set;

public interface PaymentMethodService {

    List<PaymentMethodDTO> getAllPaymentMethods ();

    PaymentMethodDTO getPaymentMethodById (Integer id);

    PaymentMethodDTO getPaymentMethodByName (String name);

    PaymentMethodDTO createNewPaymentMethod (PaymentMethodDTO paymentMethodDTO);

    PaymentMethodDTO updatePaymentMethodById (Integer id, PaymentMethodDTO paymentMethodDTO);

    PaymentMethodDTO patchPaymentMethodById (Integer id, PaymentMethodDTO paymentMethodDTO);

    void deletePaymentMethodById (Integer id);

    Set<Transaction> getPaymentMethodTransactionsById(Integer id);
}
