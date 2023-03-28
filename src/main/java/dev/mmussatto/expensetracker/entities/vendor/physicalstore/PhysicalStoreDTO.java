/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.entities.vendor.physicalstore;

import dev.mmussatto.expensetracker.entities.vendor.VendorDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class PhysicalStoreDTO extends VendorDTO {

    @NotBlank(message = "address must not be null",  groups = VendorDTO.allFieldsValidation.class)
    private String address;

    public PhysicalStoreDTO(String name, String address) {
        super(name);
        this.address = address;
    }
}
