/*
 * Created by murilo.mussatto on 20/03/2023
 */

package dev.mmussatto.expensetracker.entities.transaction;

import org.springframework.data.domain.Page;

import java.util.List;

public interface TransactionService {


    List<Transaction> getAllTransactions (); //remove

    Transaction getTransactionById (Integer id);

    Transaction createNewTransaction (Transaction transaction);

    Transaction updateTransactionById (Integer id, Transaction transaction);

    Transaction patchTransactionById (Integer id, Transaction transaction);

    void deleteTransactionById (Integer id);

    Page<Transaction> getPaginated(int page, int size);

    Page<Transaction> getTransactionsByMonth(int page, int size, String month);
}
