/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.mmussatto.expensetracker.domain.PaymentType;
import dev.mmussatto.expensetracker.domain.Transaction;
import lombok.Data;
import lombok.ToString;

import java.util.Set;

@Data
public class PaymentMethodDTO {

    private Integer id;
    private String name;
    private PaymentType type;
    @ToString.Exclude
    @JsonIgnore
    private Set<Transaction> transactions;

}
