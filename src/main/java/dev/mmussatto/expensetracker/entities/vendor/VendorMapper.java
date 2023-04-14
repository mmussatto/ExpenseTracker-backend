/*
 * Created by murilo.mussatto on 29/03/2023
 */

package dev.mmussatto.expensetracker.entities.vendor;

import dev.mmussatto.expensetracker.entities.vendor.onlinestore.OnlineStore;
import dev.mmussatto.expensetracker.entities.vendor.onlinestore.OnlineStoreDTO;
import dev.mmussatto.expensetracker.entities.vendor.physicalstore.PhysicalStore;
import dev.mmussatto.expensetracker.entities.vendor.physicalstore.PhysicalStoreDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VendorMapper {

    VendorMapper INSTANCE = Mappers.getMapper(VendorMapper.class);

    default VendorDTO convertToDTO(Vendor vendor) {
        if (vendor instanceof OnlineStore) {
            return convertToDTO((OnlineStore) vendor);
        } else if (vendor instanceof PhysicalStore) {
            return convertToDTO((PhysicalStore) vendor);
        } else {
            return null;
        }
    }

    default Vendor convertToEntity (VendorDTO vendorDTO) {
        if (vendorDTO instanceof OnlineStoreDTO) {
            return convertToEntity((OnlineStoreDTO) vendorDTO);
        } else if (vendorDTO instanceof PhysicalStoreDTO) {
            return convertToEntity((PhysicalStoreDTO) vendorDTO);
        } else {
            return null;
        }
    }



    // -------------- Online Store ----------------------------
    OnlineStoreDTO convertToDTO(OnlineStore onlineStore);
    OnlineStore convertToEntity(OnlineStoreDTO onlineStoreDTO);


    // -------------- Physical Store ----------------------------
    PhysicalStoreDTO convertToDTO(PhysicalStore physicalStore);
    PhysicalStore convertToEntity(PhysicalStoreDTO physicalStoreDTO);
}
