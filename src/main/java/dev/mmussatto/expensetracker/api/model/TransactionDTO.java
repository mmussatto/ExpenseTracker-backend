/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.mmussatto.expensetracker.domain.Category;
import dev.mmussatto.expensetracker.domain.PaymentMethod;
import dev.mmussatto.expensetracker.domain.Tag;
import dev.mmussatto.expensetracker.domain.Vendor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
public class TransactionDTO {

    // Validation Groups
    public interface onlyIdValidation {}

    public interface allFieldsValidation {}

    @Null(message = "id field must be null", groups = {onlyIdValidation.class, allFieldsValidation.class})
    private Integer id;

    private String path;

    @NotNull(message = "amount must not be blank", groups = allFieldsValidation.class)
    private Double amount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @NotNull(message = "date must not be blank", groups = allFieldsValidation.class)
    private LocalDateTime date;

    @NotBlank(message = "description must not be blank", groups = allFieldsValidation.class)
    private String description;

    @NotNull(message = "category must not be null",  groups = allFieldsValidation.class)
    private Category category;

    @NotNull(message = "paymentMethod must not be null",  groups = allFieldsValidation.class)
    private PaymentMethod paymentMethod;

    @NotNull(message = "vendor must not be null",  groups = allFieldsValidation.class)
    private Vendor vendor;

    @NotNull(message = "tags must not be null",  groups = allFieldsValidation.class)
    private Set<Tag> tags;

    public TransactionDTO(Double amount, LocalDateTime date, String description, Category category,
                       PaymentMethod paymentMethod, Vendor vendor, Set<Tag> tags) {
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.category = category;
        this.paymentMethod = paymentMethod;
        this.vendor = vendor;
        this.tags = tags;
    }
}
