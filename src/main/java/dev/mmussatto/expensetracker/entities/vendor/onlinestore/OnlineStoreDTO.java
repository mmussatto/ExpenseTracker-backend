/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.entities.vendor.onlinestore;

import dev.mmussatto.expensetracker.entities.vendor.VendorDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OnlineStoreDTO extends VendorDTO {

    @NotBlank(message = "url must not be null",  groups = VendorDTO.allFieldsValidation.class)
    private String url;

    public OnlineStoreDTO(String name, String url) {
        super(name, "Online Store");
        this.url = url;
    }

    public OnlineStoreDTO() {
        setType("Online Store");
    }
}
