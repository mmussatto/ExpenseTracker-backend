/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.api.v1.mappers;

import dev.mmussatto.expensetracker.api.v1.model.PhysicalStoreDTO;
import dev.mmussatto.expensetracker.domain.PhysicalStore;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PhysicalStoreMapper {

    PhysicalStoreMapper INSTANCE = Mappers.getMapper(PhysicalStoreMapper.class);

    PhysicalStoreDTO physicalStoreToPhysicalStoreDTO (PhysicalStore physicalStore);
}
