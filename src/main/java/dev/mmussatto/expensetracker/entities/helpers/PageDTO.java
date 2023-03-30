/*
 * Created by murilo.mussatto on 30/03/2023
 */

package dev.mmussatto.expensetracker.entities.helpers;

import lombok.Data;

import java.util.List;

@Data
public class PageDTO<T> {

    private List<T> content;

    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private String nextPage;
    private String previousPage;

}
