/*
 * Created by murilo.mussatto on 15/03/2023
 */

package dev.mmussatto.expensetracker.entities.vendor;

import dev.mmussatto.expensetracker.entities.transaction.Transaction;
import org.springframework.data.domain.Page;

import java.util.List;

public interface VendorService<V extends Vendor> {

    List<V> getAllVendors ();

    V getVendorById (Integer id);

    V getVendorByName (String name);

    V createNewVendor (V vendor);

    V updateVendorById (Integer id, V vendor);

    V patchVendorById (Integer id, V vendor);

    void deleteVendorById (Integer id);

    Page<Transaction> getTransactionsByVendorId(Integer id, int page, int size);
}
