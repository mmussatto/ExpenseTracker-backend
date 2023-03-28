/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.entities.vendor.physicalstore;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PhysicalStoreMapper {

    PhysicalStoreMapper INSTANCE = Mappers.getMapper(PhysicalStoreMapper.class);

    PhysicalStoreDTO convertToDTO(PhysicalStore physicalStore);

    PhysicalStore convertToEntity(PhysicalStoreDTO physicalStoreDTO);
}
