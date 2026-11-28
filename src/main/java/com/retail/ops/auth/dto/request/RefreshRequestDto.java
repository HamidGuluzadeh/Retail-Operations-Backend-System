package com.retail.ops.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequestDto(@NotBlank(message = "Refresh token daxil edilməlidir!")
                                String refreshToken) {

}
