package com.retail.ops.staff.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateEmployeeRequestDto(@Size(min = 2, max = 20, message = "Ad 2-20 simvol aralığında olmalıdır")
                                       String firstName,
                                       @Size(min = 2, max = 30, message = "Soyad 2-30 simvol aralığında olmalıdır")
                                       String lastName,
                                       @Size(max = 25, message = "Telefon nömrəsi maksimum 25 simvol ola bilər")
                                       String phoneNumber,
                                       @Email(message = "Email formatı yanlışdır")
                                       @Size(max = 50, message = "Email maksimum 50 simvol ola bilər")
                                       String email) {

}
