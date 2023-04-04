/*
 * Created by murilo.mussatto on 24/02/2023
 */

package dev.mmussatto.expensetracker.entities.paymentmethod;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.mmussatto.expensetracker.entities.transaction.Transaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"transactions"})
@Entity
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String name;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private PaymentType type;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "paymentMethod")
    @ToString.Exclude
    @JsonIgnore
    private List<Transaction> transactions = new ArrayList<>();

    public PaymentMethod(String name, PaymentType type) {
        this.name = name;
        this.type = type;
    }

    public PaymentMethod(Integer id) {
        this.id = id;
    }
}
