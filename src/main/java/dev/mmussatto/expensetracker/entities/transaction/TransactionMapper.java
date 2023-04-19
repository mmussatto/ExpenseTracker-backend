/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.entities.transaction;

import dev.mmussatto.expensetracker.entities.category.Category;
import dev.mmussatto.expensetracker.entities.paymentmethod.PaymentMethod;
import dev.mmussatto.expensetracker.entities.tag.Tag;
import dev.mmussatto.expensetracker.entities.vendor.defaultvendor.DefaultVendor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper
public interface TransactionMapper {

    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    TransactionDTO convertToDTO(Transaction transaction);

    Transaction convertToEntity(TransactionDTO transactionDTO);


    // -------------- Transaction Request ----------------------------
    //Request Mapper
    @Mapping(target = "category", source = "source.categoryId")
    @Mapping(target = "paymentMethod", source = "source.paymentMethodId")
    @Mapping(target = "vendor", source = "source.vendorId")
    @Mapping(target = "tags", source = "source.tagIds")
    Transaction convertRequestToEntity (RequestTransactionDTO source);


    //Map objects inside request
    @Mapping(target = "id", source = "categoryId")
    Category mapCategory(Integer categoryId);

    @Mapping(target = "id", source = "paymentMethodId")
    PaymentMethod mapPaymentMethod (Integer paymentMethodId);

    @Mapping(target = "id", source = "tagId")
    Tag mapTags (Integer tagId);
    @Mapping(target = "tag.id", source = "tagIds")
    Set<Tag> mapTagSet (Set<Integer> tagIds);

    @Mapping(target = "id", source = "vendorId")
    DefaultVendor mapVendor (Integer vendorId);

}
