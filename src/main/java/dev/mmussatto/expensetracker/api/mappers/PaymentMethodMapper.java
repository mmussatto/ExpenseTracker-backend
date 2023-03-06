/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.api.mappers;

import dev.mmussatto.expensetracker.api.model.PaymentMethodDTO;
import dev.mmussatto.expensetracker.domain.PaymentMethod;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PaymentMethodMapper {

    PaymentMethodMapper INSTANCE = Mappers.getMapper(PaymentMethodMapper.class);

    PaymentMethodDTO convertToDTO(PaymentMethod paymentMethod);

    PaymentMethod convertToEntity(PaymentMethodDTO paymentMethodDTO);
}
