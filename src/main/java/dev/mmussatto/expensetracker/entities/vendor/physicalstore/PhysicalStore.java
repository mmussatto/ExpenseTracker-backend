/*
 * Created by murilo.mussatto on 27/02/2023
 */

package dev.mmussatto.expensetracker.entities.vendor.physicalstore;

import dev.mmussatto.expensetracker.entities.vendor.Vendor;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
@Entity
public class PhysicalStore extends Vendor {

    private String address;

    public PhysicalStore(String name, String address) {
        super(name);
        this.address = address;
    }
}
