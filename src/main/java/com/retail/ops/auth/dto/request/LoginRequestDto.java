package com.retail.ops.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(@NotBlank(message = "İstifadəçi adı daxil edilməlidir!")
                              String username,
                              @NotBlank(message = "Şifrə daxil edilməlidir!")
                              @Size(min = 6, message = "Şifrə ən az 6 simvoldan ibarət ola bilər!")
                              String password) {

}
