/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.entities.vendor.onlinestore;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OnlineStoreMapper {

    OnlineStoreMapper INSTANCE = Mappers.getMapper(OnlineStoreMapper.class);

    OnlineStoreDTO convertToDTO(OnlineStore onlineStore);

    OnlineStore convertToEntity(OnlineStoreDTO onlineStoreDTO);
}
