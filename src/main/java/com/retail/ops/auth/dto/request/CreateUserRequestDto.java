package com.retail.ops.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequestDto(@NotBlank(message = "İşçi ID daxil edilməlidir")
                                   String employeeId,
                                   @NotBlank(message = "Şifrə daxil edilməlidir")
                                   @Size(min = 6, message = "Şifrə ən az 6 simvoldan ibarət ola bilər")
                                   String password) {

}
