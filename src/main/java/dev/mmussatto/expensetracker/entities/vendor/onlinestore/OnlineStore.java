/*
 * Created by murilo.mussatto on 24/02/2023
 */

package dev.mmussatto.expensetracker.entities.vendor.onlinestore;

import dev.mmussatto.expensetracker.entities.vendor.Vendor;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Entity
public class OnlineStore extends Vendor {

    private String url;

    public OnlineStore(String name, String url) {
        super(name);
        this.url = url;
    }
}
