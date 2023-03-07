/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.mmussatto.expensetracker.domain.PaymentType;
import dev.mmussatto.expensetracker.domain.Transaction;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class PaymentMethodDTO {

    private Integer id;
    @NotNull
    private String name;
    @NotNull
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
