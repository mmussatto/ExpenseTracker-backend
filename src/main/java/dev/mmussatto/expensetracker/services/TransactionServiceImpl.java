/*
 * Created by murilo.mussatto on 20/03/2023
 */

package dev.mmussatto.expensetracker.services;

import dev.mmussatto.expensetracker.domain.*;
import dev.mmussatto.expensetracker.repositories.TransactionRepository;
import dev.mmussatto.expensetracker.services.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final CategoryService categoryService;
    private final PaymentMethodService paymentMethodService;
    private final VendorService<Vendor> vendorService;
    private final TagService tagService;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  CategoryService categoryService,
                                  PaymentMethodService paymentMethodService,
                                  VendorService<Vendor> vendorService,
                                  TagService tagService) {
        this.transactionRepository = transactionRepository;
        this.categoryService = categoryService;
        this.paymentMethodService = paymentMethodService;
        this.vendorService = vendorService;
        this.tagService = tagService;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction getTransactionById(Integer id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Transaction %d not found!", id)));
    }

    @Override
    public Transaction createNewTransaction(Transaction transaction) {
        checkIfEntitiesExist(transaction);

        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction updateTransactionById(Integer id, Transaction transaction) {
        transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Transaction %d not found!", id)));

        checkIfEntitiesExist(transaction);

        transaction.setId(id);

        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction patchTransactionById(Integer id, Transaction transaction) {
        return transactionRepository.findById(id).map(savedEntity -> {

            if (transaction.getAmount() != null)
                savedEntity.setAmount(transaction.getAmount());

            if (transaction.getDate() != null)
                savedEntity.setDate(transaction.getDate());


            if (transaction.getDescription() != null)
                savedEntity.setDescription(transaction.getDescription());

            if (transaction.getPaymentMethod() != null) {
                PaymentMethod pm = paymentMethodService.getPaymentMethodById(transaction.getPaymentMethod().getId());

                savedEntity.setPaymentMethod(pm);
            }

            if (transaction.getCategory() != null) {
                Category category = categoryService.getCategoryById(transaction.getCategory().getId());

                savedEntity.setCategory(category);
            }

            if (!transaction.getTags().isEmpty()) {
                savedEntity.getTags().clear();

                transaction.getTags().forEach(transactionTag -> {
                    Tag savedTag = tagService.getTagById(transactionTag.getId());

                    savedEntity.getTags().add(savedTag);
                });
            }

            if (transaction.getVendor() != null) {
                Vendor vendor = vendorService.getVendorById(transaction.getVendor().getId());

                savedEntity.setVendor(vendor);
            }

            return transactionRepository.save(savedEntity);

        }).orElseThrow(() -> new ResourceNotFoundException(String.format("Transaction %d not found!", id)));
    }

    @Override
    public void deleteTransactionById(Integer id) {
        transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Transaction %d not found!", id)));

        transactionRepository.deleteById(id);
    }

    @Override
    public Set<Transaction> getTransactionsByCategory(Integer categoryId) {
        return categoryService.getTransactionsById(categoryId);
    }


    private void checkIfEntitiesExist(Transaction transaction) {
        categoryService.getCategoryById(transaction.getCategory().getId());
        paymentMethodService.getPaymentMethodById(transaction.getPaymentMethod().getId());
        vendorService.getVendorById(transaction.getVendor().getId());
        transaction.getTags().forEach(tag -> tagService.getTagById(tag.getId()));
    }
}
