/*
 * Created by murilo.mussatto on 27/02/2023
 */

package dev.mmussatto.expensetracker.entities.transaction;

import dev.mmussatto.expensetracker.entities.category.Category;
import dev.mmussatto.expensetracker.entities.paymentmethod.PaymentMethod;
import dev.mmussatto.expensetracker.entities.tag.Tag;
import dev.mmussatto.expensetracker.entities.vendor.Vendor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private Double amount;

    @NotNull
    private LocalDateTime date;

    @Column(columnDefinition="TEXT")
    private String description;

    @NotNull
    @ManyToOne
    private Category category;

    @NotNull
    @ManyToOne
    private PaymentMethod paymentMethod;

    @NotNull
    @ManyToOne
    private Vendor vendor;

    @NotNull
    @ManyToMany
    @JoinTable(name = "transaction_tags",
            joinColumns = @JoinColumn(name = "transaction_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();


    public Transaction(Double amount, LocalDateTime date, String description, Category category,
                       PaymentMethod paymentMethod, Vendor vendor, Set<Tag> tags) {
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.category = category;
        this.paymentMethod = paymentMethod;
        this.vendor = vendor;
        this.tags = tags;
    }

}
