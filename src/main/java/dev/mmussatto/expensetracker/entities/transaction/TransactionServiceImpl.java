/*
 * Created by murilo.mussatto on 20/03/2023
 */

package dev.mmussatto.expensetracker.entities.transaction;

import dev.mmussatto.expensetracker.entities.category.Category;
import dev.mmussatto.expensetracker.entities.category.CategoryService;
import dev.mmussatto.expensetracker.entities.paymentmethod.PaymentMethod;
import dev.mmussatto.expensetracker.entities.paymentmethod.PaymentMethodService;
import dev.mmussatto.expensetracker.entities.tag.Tag;
import dev.mmussatto.expensetracker.entities.tag.TagService;
import dev.mmussatto.expensetracker.entities.vendor.Vendor;
import dev.mmussatto.expensetracker.entities.vendor.VendorService;
import dev.mmussatto.expensetracker.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public Page<Transaction> getPaginated(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("date"));

        Page<Transaction> pagedResult = transactionRepository.findAll(pageable);

        if(pagedResult.hasContent())
            return pagedResult;
        else
            throw new RuntimeException("page with no content");
    }


    private void checkIfEntitiesExist(Transaction transaction) {
        Category category = categoryService.getCategoryById(transaction.getCategory().getId());
        transaction.setCategory(category);

        PaymentMethod paymentMethod = paymentMethodService.getPaymentMethodById(transaction.getPaymentMethod().getId());
        transaction.setPaymentMethod(paymentMethod);

        Vendor vendor = vendorService.getVendorById(transaction.getVendor().getId());
        transaction.setVendor(vendor);

        Set<Tag> tags = transaction.getTags().stream()
                .map(tag -> tagService.getTagById(tag.getId())).collect(Collectors.toSet());
        transaction.setTags(tags);
    }
}
