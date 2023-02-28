/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.api.v1.mappers;

import dev.mmussatto.expensetracker.api.v1.model.OnlineStoreDTO;
import dev.mmussatto.expensetracker.domain.OnlineStore;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OnlineStoreMapper {

    OnlineStoreMapper INSTANCE = Mappers.getMapper(OnlineStoreMapper.class);

    OnlineStoreDTO onlineStoreToOnlineStoreDTO (OnlineStore onlineStore);
}
