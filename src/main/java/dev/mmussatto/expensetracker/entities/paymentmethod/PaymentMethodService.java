/*
 * Created by murilo.mussatto on 03/03/2023
 */

package dev.mmussatto.expensetracker.entities.paymentmethod;

import dev.mmussatto.expensetracker.entities.transaction.Transaction;

import java.util.List;
import java.util.Set;

public interface PaymentMethodService {

    List<PaymentMethod> getAllPaymentMethods ();

    PaymentMethod getPaymentMethodById (Integer id);

    PaymentMethod getPaymentMethodByName (String name);

    PaymentMethod createNewPaymentMethod (PaymentMethod paymentMethod);

    PaymentMethod updatePaymentMethodById (Integer id, PaymentMethod paymentMethod);

    PaymentMethod patchPaymentMethodById (Integer id, PaymentMethod paymentMethod);

    void deletePaymentMethodById (Integer id);

    Set<Transaction> getPaymentMethodTransactionsById(Integer id);
}
