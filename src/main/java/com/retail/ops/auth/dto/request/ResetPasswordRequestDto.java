package com.retail.ops.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDto(@NotBlank(message = "Yeni şifrə daxil edilməlidir")
                                      @Size(min = 6, message = "Yeni şifrə ən az 6 simvoldan ibarət ola bilər")
                                      String newPassword) {

}
