package com.retail.ops.staff.dto.request;

import com.retail.ops.staff.model.StoreFormat;
import jakarta.validation.constraints.Size;

public record UpdateStoreRequestDto(@Size(max = 100, message = "Mağaza adı maksimum 100 simvol ola bilər")
                                    String name,
                                    StoreFormat format) {

}
