/*
 * Created by murilo.mussatto on 17/04/2023
 */

package dev.mmussatto.expensetracker.entities.vendor.defaultvendor;

import dev.mmussatto.expensetracker.entities.vendor.VendorDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DefaultVendorDTO extends VendorDTO {

    public DefaultVendorDTO(Integer id) {
        super.setId(id);
    }

    public DefaultVendorDTO(String name) {
        this.setName(name);
    }
}
