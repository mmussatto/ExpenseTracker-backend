/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.entities.vendor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dev.mmussatto.expensetracker.entities.transaction.Transaction;
import dev.mmussatto.expensetracker.entities.vendor.onlinestore.OnlineStoreDTO;
import dev.mmussatto.expensetracker.entities.vendor.physicalstore.PhysicalStoreDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION,
        property = "type",
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = OnlineStoreDTO.class, name = "Online Store"),
        @JsonSubTypes.Type(value = PhysicalStoreDTO.class, name = "Physical Store")
})
public abstract class VendorDTO {

    // Validation Groups
    public interface onlyIdValidation {}

    public interface allFieldsValidation {}

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Null(message = "id field must be null", groups = {onlyIdValidation.class, allFieldsValidation.class})
    private Integer id;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String path;

    private String type;

    @NotBlank(message = "name must not be blank", groups = allFieldsValidation.class)
    private String name;

    @ToString.Exclude
    @JsonIgnore //for transactions, use the /transactions endpoint (returns a TransactionDTO)
    private List<Transaction> transactions = new ArrayList<>();

    public VendorDTO(String name, String type) {
        this.name = name;
        this.type = type;
    }
}
