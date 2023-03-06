/*
 * Created by murilo.mussatto on 24/02/2023
 */

package dev.mmussatto.expensetracker.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = {"transactions"})
@NoArgsConstructor
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String name;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private Color color;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "category")
    @ToString.Exclude
    @JsonIgnore
    private Set<Transaction> transactions = new HashSet<>();


    public Category(String name, Color color) {
        this.name = name;
        this.color = color;
    }
}
