/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.mmussatto.expensetracker.domain.Color;
import dev.mmussatto.expensetracker.domain.Transaction;
import lombok.Data;
import lombok.ToString;

import java.util.Set;

@Data
public class TagDTO {

    private Integer id;
    private String name;
    private Color color;
    @ToString.Exclude
    @JsonIgnore
    private Set<Transaction> transactions;
}
