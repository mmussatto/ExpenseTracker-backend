/*
 * Created by murilo.mussatto on 24/02/2023
 */

package dev.mmussatto.expensetracker.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"transactions"})
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "store")
    private Set<Transaction> transactions = new HashSet<>();

    public Store(String name) {
        this.name = name;
    }
}
