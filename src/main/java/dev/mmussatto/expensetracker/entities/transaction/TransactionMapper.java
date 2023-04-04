/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.entities.transaction;

import dev.mmussatto.expensetracker.entities.category.Category;
import dev.mmussatto.expensetracker.entities.paymentmethod.PaymentMethod;
import dev.mmussatto.expensetracker.entities.tag.Tag;
import dev.mmussatto.expensetracker.entities.vendor.Vendor;
import dev.mmussatto.expensetracker.entities.vendor.defaultvendor.DefaultVendor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TransactionMapper {

    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    TransactionDTO convertToDTO(Transaction transaction);

    Transaction convertToEntity(TransactionDTO transactionDTO);


    //Request Mappers
    @Mapping(target = "category", source = "source.categoryId")
    @Mapping(target = "paymentMethod", source = "source.paymentMethodId")
    @Mapping(target = "vendor", source = "source", qualifiedByName="mapVendorWithIdAndType")
    @Mapping(target = "tags", source = "source.tagIds")
    Transaction convertRequestToEntity (RequestTransactionDTO source);


    //Map objects inside request
    @Mapping(target = "id", source = "categoryId")
    Category mapCategory(Integer categoryId);

    @Mapping(target = "id", source = "paymentMethodId")
    PaymentMethod mapPaymentMethod (Integer paymentMethodId);

    @Mapping(target = "id", source = "tagId")
    Tag mapTags (Integer tagId);

    @Named("mapVendorWithIdAndType")
    default Vendor mapVendor (RequestTransactionDTO source) {
        return source.getVendorId() == null ? null : new DefaultVendor(source.getVendorId());
    }

}
