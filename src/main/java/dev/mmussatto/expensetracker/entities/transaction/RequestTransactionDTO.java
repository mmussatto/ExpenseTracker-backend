/*
 * Created by murilo.mussatto on 04/04/2023
 */

package dev.mmussatto.expensetracker.entities.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
public class RequestTransactionDTO {

    // Validation Groups
    public interface onlyIdValidation {}

    public interface allFieldsValidation {}

    @NotNull(message = "amount must not be blank", groups = allFieldsValidation.class)
    private Double amount;

    @Schema(type = "string", example = "2023-04-24T08:30:00", pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @NotNull(message = "date must not be blank", groups = allFieldsValidation.class)
    private LocalDateTime date;

    @NotBlank(message = "description must not be blank", groups = allFieldsValidation.class)
    private String description;

    @NotNull(message = "categoryId must not be null",  groups = allFieldsValidation.class)
    private Integer categoryId;

    @NotNull(message = "paymentMethodId must not be null",  groups = allFieldsValidation.class)
    private Integer paymentMethodId;

    @NotNull(message = "vendorId must not be null",  groups = allFieldsValidation.class)
    private Integer vendorId;

    @NotNull(message = "tagIds must not be null",  groups = allFieldsValidation.class)
    private Set<Integer> tagIds;

    public RequestTransactionDTO(Double amount, LocalDateTime date, String description,
                                 Integer categoryId, Integer paymentMethodId, Integer vendorId, Set<Integer> tagIds) {
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.categoryId = categoryId;
        this.paymentMethodId = paymentMethodId;
        this.vendorId = vendorId;
        this.tagIds = tagIds;
    }
}
