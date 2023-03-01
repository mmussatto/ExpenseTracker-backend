/*
 * Created by murilo.mussatto on 24/02/2023
 */

package dev.mmussatto.expensetracker.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

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
    private Set<Transaction> transactions = new HashSet<>();

    public PaymentMethod(String name, PaymentType type) {
        this.name = name;
        this.type = type;
    }
}
