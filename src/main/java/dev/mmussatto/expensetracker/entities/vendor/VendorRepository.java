/*
 * Created by murilo.mussatto on 27/02/2023
 */

package dev.mmussatto.expensetracker.entities.vendor;

import dev.mmussatto.expensetracker.entities.vendor.onlinestore.OnlineStore;
import dev.mmussatto.expensetracker.entities.vendor.physicalstore.PhysicalStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VendorRepository<T extends Vendor> extends JpaRepository<T, Integer> {

    Optional<T> findByName (String name);

    Optional<OnlineStore> findByUrl (String url);

    Optional<PhysicalStore> findByAddress (String address);
}
