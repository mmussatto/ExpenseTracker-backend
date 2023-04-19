/*
 * Created by murilo.mussatto on 15/03/2023
 */

package dev.mmussatto.expensetracker.entities.vendor;

import dev.mmussatto.expensetracker.entities.transaction.Transaction;
import org.springframework.data.domain.Page;

import java.util.List;

public interface VendorService {

    List<Vendor> getAllVendors ();

    Vendor getVendorById (Integer id);

    Vendor getVendorByName (String name);

    Vendor createNewVendor (Vendor vendor);

    Vendor updateVendorById (Integer id, Vendor vendor);

    Vendor patchVendorById (Integer id, Vendor vendor);

    void deleteVendorById (Integer id);

    Page<Transaction> getTransactionsByVendorId(Integer id, int page, int size);
}
