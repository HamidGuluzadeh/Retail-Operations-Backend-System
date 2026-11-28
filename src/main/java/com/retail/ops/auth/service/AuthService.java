package com.retail.ops.auth.service;

import com.retail.ops.auth.dto.request.LoginRequestDto;
import com.retail.ops.auth.dto.request.RefreshRequestDto;
import com.retail.ops.auth.dto.response.AuthResponseDto;

public interface AuthService {

    AuthResponseDto login(LoginRequestDto loginRequestDto);

    AuthResponseDto refresh(RefreshRequestDto refreshRequestDto);

    void logout();

}
