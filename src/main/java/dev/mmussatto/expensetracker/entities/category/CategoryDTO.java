/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.entities.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.mmussatto.expensetracker.entities.helpers.Color;
import dev.mmussatto.expensetracker.entities.transaction.Transaction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CategoryDTO {

    // Validation Groups
    public interface onlyIdValidation {}
    public interface allFieldsValidation {}

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Null(message = "id field must be null", groups = {onlyIdValidation.class, allFieldsValidation.class})
    private Integer id;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String path;

    @NotBlank(message = "name must not be blank", groups = allFieldsValidation.class)
    private String name;

    @NotNull(message = "color must not be null", groups = allFieldsValidation.class)
    private Color color;

    @ToString.Exclude
    @JsonIgnore
    private List<Transaction> transactions = new ArrayList<>();


    public CategoryDTO(String name, Color color) {
        this.name = name;
        this.color = color;
    }
}
