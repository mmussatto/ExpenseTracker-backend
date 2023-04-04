/*
 * Created by murilo.mussatto on 04/04/2023
 */

package dev.mmussatto.expensetracker.entities.vendor.defaultvendor;

import dev.mmussatto.expensetracker.entities.vendor.Vendor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DefaultVendor extends Vendor {

    public DefaultVendor(Integer id) {
        super.setId(id);
    }
}
