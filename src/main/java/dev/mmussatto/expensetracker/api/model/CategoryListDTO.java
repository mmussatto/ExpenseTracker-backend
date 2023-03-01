/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.api.model;


import lombok.Data;


import java.util.List;

@Data
public class CategoryListDTO {

    private Integer numberOfCategories;

    private List<CategoryDTO> categories;

    public CategoryListDTO(List<CategoryDTO> categories) {
        this.categories = categories;
        this.numberOfCategories = categories.size();
    }
}
