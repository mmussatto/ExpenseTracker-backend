/*
 * Created by murilo.mussatto on 20/03/2023
 */

package dev.mmussatto.expensetracker.services;

import dev.mmussatto.expensetracker.domain.Transaction;

import java.util.List;
import java.util.Set;

public interface TransactionService {


    List<Transaction> getAllTransactions ();

    Transaction getTransactionById (Integer id);

    Transaction createNewTransaction (Transaction transaction);

    Transaction updateTransactionById (Integer id, Transaction transaction);

    Transaction patchTransactionById (Integer id, Transaction transaction);

    void deleteTransactionById (Integer id);

    Set<Transaction> getTransactionsByCategory(Integer categoryId);
}
