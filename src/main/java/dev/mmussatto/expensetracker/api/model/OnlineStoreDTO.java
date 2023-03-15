/*
 * Created by murilo.mussatto on 28/02/2023
 */

package dev.mmussatto.expensetracker.api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OnlineStoreDTO extends VendorDTO {

    @NotBlank(message = "url must not be null",  groups = VendorDTO.allFieldsValidation.class)
    private String url;
}
