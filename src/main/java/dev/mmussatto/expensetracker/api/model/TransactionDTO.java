/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.mmussatto.expensetracker.domain.Category;
import dev.mmussatto.expensetracker.domain.PaymentMethod;
import dev.mmussatto.expensetracker.domain.Tag;
import dev.mmussatto.expensetracker.domain.Vendor;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Set;

@Data
public class TransactionDTO {

    private Integer id;

    private String path;

    private Double amount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Timestamp date;

    private String description;

    private PaymentMethod paymentMethod;

    private Category category;

    private Set<Tag> tags;

    private Vendor vendor;

}
