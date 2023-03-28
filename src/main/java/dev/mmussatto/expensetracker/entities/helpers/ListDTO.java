/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.entities.helpers;


import lombok.Data;

import java.util.List;

@Data
public class ListDTO<T> {

    private Integer numberOfItems;

    private List<T> items;

    public ListDTO(List<T> items) {
        this.items = items;
        this.numberOfItems = items.size();
    }
}
