/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.api.v1.mappers;

import dev.mmussatto.expensetracker.api.v1.model.CategoryDTO;
import dev.mmussatto.expensetracker.domain.Category;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    CategoryDTO categoryToCategoryDTO (Category category);

    Category categoryDTOToCategory (CategoryDTO categoryDTO);
}
