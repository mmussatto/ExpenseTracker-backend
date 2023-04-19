/*
 * Created by murilo.mussatto on 24/02/2023
 */

package dev.mmussatto.expensetracker.entities.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.mmussatto.expensetracker.entities.helpers.Color;
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
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private List<Transaction> transactions = new ArrayList<>();


    public Category(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public Category(Integer id) {
        this.id = id;
    }
}
