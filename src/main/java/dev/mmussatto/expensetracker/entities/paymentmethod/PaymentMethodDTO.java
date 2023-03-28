/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.entities.paymentmethod;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.mmussatto.expensetracker.entities.transaction.Transaction;
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
public class PaymentMethodDTO {
    // Validation Groups
    public interface onlyIdValidation {}

    public interface allFieldsValidation {}

    @Null(message = "id field must be null", groups = {onlyIdValidation.class, allFieldsValidation.class})
    private Integer id;

    private String path;

    @NotBlank(message = "name must not be blank", groups = allFieldsValidation.class)
    private String name;

    @NotNull(message = "type must not be null",  groups = allFieldsValidation.class)
    private PaymentType type;

    @ToString.Exclude
    @JsonIgnore //for transactions, use the /transactions endpoint (returns a TransactionDTO)
    private Set<Transaction> transactions = new HashSet<>();

    public PaymentMethodDTO(String name, PaymentType type) {
        this.name = name;
        this.type = type;
    }
}
