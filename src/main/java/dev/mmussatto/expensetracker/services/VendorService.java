/*
 * Created by murilo.mussatto on 15/03/2023
 */

package dev.mmussatto.expensetracker.services;

import dev.mmussatto.expensetracker.domain.Transaction;
import dev.mmussatto.expensetracker.domain.Vendor;

import java.util.List;
import java.util.Set;

public interface VendorService<V extends Vendor> {

    List<V> getAllVendors ();

    V getVendorById (Integer id);

    V getVendorByName (String name);

    V createNewVendor (V vendor);

    V updateVendorById (Integer id, V vendor);

    V patchVendorById (Integer id, V vendor);

    void deleteVendorById (Integer id);

    Set<Transaction> getTransactionsById(Integer id);
}
