package com.retail.ops.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequestDto(@Size(min = 6, message = "Köhnə şifrə ən az 6 simvoldan ibarət ola bilər")
                                       @NotBlank(message = "Köhnə şifrə daxil edilməlidir")
                                       String oldPassword,
                                       @NotBlank(message = "Yeni şifrə daxil edilməlidir")
                                       @Size(min = 6, message = "Yeni şifrə ən az 6 simvoldan ibarət ola bilər")
                                       String newPassword) {

}
