/*
 * Created by murilo.mussatto on 27/02/2023
 */

package dev.mmussatto.expensetracker.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
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

    private Timestamp date;

    @Lob
    private String description;


    @ManyToOne
    private PaymentMethod paymentMethod;

    @ManyToOne
    private Category category;

    @ManyToMany
    @JoinTable(name = "transaction_tags",
            joinColumns = @JoinColumn(name = "transaction_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    @ManyToOne
    private Vendor vendor;

    public Transaction(Double amount, Timestamp date, String description, PaymentMethod paymentMethod,
                       Category category, Set<Tag> tags, Vendor vendor) {
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.paymentMethod = paymentMethod;
        this.category = category;
        this.tags = tags;
        this.vendor = vendor;
    }
}
