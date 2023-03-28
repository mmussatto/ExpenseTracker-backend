/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.entities.vendor.onlinestore;

import dev.mmussatto.expensetracker.entities.vendor.VendorDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class OnlineStoreDTO extends VendorDTO {

    @NotBlank(message = "url must not be null",  groups = VendorDTO.allFieldsValidation.class)
    private String url;

    public OnlineStoreDTO(String name, String url) {
        super(name);
        this.url = url;
    }
}
