package com.retail.ops.staff.dto.request;

import com.retail.ops.staff.model.StoreFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateStoreRequestDto(@NotBlank(message = "Mağaza kodu boş ola bilməz")
                                    @Size(max = 20, message = "Mağaza kodu maksimum 20 simvol ola bilər")
                                    String code,
                                    @NotBlank(message = "Mağaza adı boş ola bilməz")
                                    @Size(max = 100, message = "Mağaza adı maksimum 100 simvol ola bilə!")
                                    String name,
                                    @NotNull(message = "Mağaza formatı seçilməlidir")
                                    StoreFormat format) {

}
