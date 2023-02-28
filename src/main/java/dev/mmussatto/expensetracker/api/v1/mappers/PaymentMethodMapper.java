/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.api.v1.mappers;

import dev.mmussatto.expensetracker.api.v1.model.PaymentMethodDTO;
import dev.mmussatto.expensetracker.domain.PaymentMethod;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PaymentMethodMapper {

    PaymentMethodMapper INSTANCE = Mappers.getMapper(PaymentMethodMapper.class);

    PaymentMethodDTO paymentMethodToPaymentMethodDTO(PaymentMethod paymentMethod);
}
