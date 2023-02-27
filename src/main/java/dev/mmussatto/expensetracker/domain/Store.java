/*
 * Created by murilo.mussatto on 24/02/2023
 */

package dev.mmussatto.expensetracker.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String name;
}
