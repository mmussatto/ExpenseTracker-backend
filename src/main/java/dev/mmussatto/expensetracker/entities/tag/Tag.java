/*
 * Created by murilo.mussatto on 24/02/2023
 */

package dev.mmussatto.expensetracker.entities.tag;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.mmussatto.expensetracker.entities.helpers.Color;
import dev.mmussatto.expensetracker.entities.transaction.Transaction;
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
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String name;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private Color color;

    @ManyToMany(mappedBy = "tags")
    @ToString.Exclude
    @JsonIgnore
    private Set<Transaction> transactions = new HashSet<>();

    public Tag(String name, Color color) {
        this.name = name;
        this.color = color;
    }
}
