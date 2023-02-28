/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.api.v1.model;

import dev.mmussatto.expensetracker.domain.Transaction;
import lombok.Data;

import java.util.Set;

@Data
public abstract class StoreDTO {
    private Integer id;
    private String name;
    private Set<Transaction> transactions;
}
