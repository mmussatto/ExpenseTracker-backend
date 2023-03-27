/*
 * Created by murilo.mussatto on 27/02/2023
 */

package dev.mmussatto.expensetracker.domain;

import jakarta.persistence.*;
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

    private Double amount;

    private LocalDateTime date;

    @Lob
    private String description;

    @ManyToOne
    private Category category;

    @ManyToOne
    private PaymentMethod paymentMethod;

    @ManyToOne
    private Vendor vendor;

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
