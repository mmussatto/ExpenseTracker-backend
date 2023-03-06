/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.mmussatto.expensetracker.domain.PaymentType;
import dev.mmussatto.expensetracker.domain.Transaction;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class PaymentMethodDTO {

    private Integer id;
    private String name;
    private PaymentType type;
    @ToString.Exclude
    @JsonIgnore
    private Set<Transaction> transactions = new HashSet<>();
    private String path;

    public PaymentMethodDTO(String name, PaymentType type) {
        this.name = name;
        this.type = type;
    }
}
