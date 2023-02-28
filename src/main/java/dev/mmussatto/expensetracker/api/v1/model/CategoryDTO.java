/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.api.v1.model;

import dev.mmussatto.expensetracker.domain.Color;
import dev.mmussatto.expensetracker.domain.Transaction;
import lombok.Data;

import java.util.Set;

@Data
public class CategoryDTO {
    private Integer Id;
    private String name;
    private Color color;
    private Set<Transaction> transactions;

}
