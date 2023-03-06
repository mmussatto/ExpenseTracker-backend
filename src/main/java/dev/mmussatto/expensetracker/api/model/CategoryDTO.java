/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.mmussatto.expensetracker.domain.Color;
import dev.mmussatto.expensetracker.domain.Transaction;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class CategoryDTO {
    private Integer id;
    @NotNull
    private String name;
    @NotNull
    private Color color;
    @ToString.Exclude
    @JsonIgnore
    private Set<Transaction> transactions = new HashSet<>();
    private String path;

    public CategoryDTO(String name, Color color) {
        this.name = name;
        this.color = color;
    }
}
