/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.api.v1.model;

import dev.mmussatto.expensetracker.domain.PaymentType;
import dev.mmussatto.expensetracker.domain.Transaction;
import lombok.Data;

import java.util.Set;

@Data
public class PaymentMethodDTO {

    private Integer id;
    private String name;
    private PaymentType type;
    private Set<Transaction> transactions;

}
