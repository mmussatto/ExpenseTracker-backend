/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.mmussatto.expensetracker.domain.Color;
import dev.mmussatto.expensetracker.domain.Transaction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class TagDTO {

    // Validation Groups
    public interface onlyIdValidation {}

    public interface allFieldsValidation {}

    @Null(message = "id field must be null", groups = {onlyIdValidation.class, allFieldsValidation.class})
    private Integer id;

    private String path;

    @NotBlank(message = "name must not be blank", groups = allFieldsValidation.class)
    private String name;

    @NotNull(message = "color must not be null",  groups = allFieldsValidation.class)
    private Color color;

    @ToString.Exclude
    @JsonIgnore //for transactions, use the /transactions endpoint (returns a TransactionDTO)
    private Set<Transaction> transactions = new HashSet<>();

    public TagDTO(String name, Color color) {
        this.name = name;
        this.color = color;
    }
}
