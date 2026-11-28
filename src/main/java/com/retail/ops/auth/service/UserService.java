package com.retail.ops.auth.service;

import com.retail.ops.auth.dto.request.ChangePasswordRequestDto;
import com.retail.ops.auth.dto.request.CreateUserRequestDto;
import com.retail.ops.auth.dto.request.ResetPasswordRequestDto;
import com.retail.ops.auth.dto.response.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    Page<UserResponseDto> getUsersByStoreId(String storeId, Pageable pageable);

    Page<UserResponseDto> getUsersByStoreCode(String storeCode, Pageable pageable);

    UserResponseDto getUserById(String userId);

    UserResponseDto getMe();

    UserResponseDto createUser(CreateUserRequestDto request);

    void changePassword(ChangePasswordRequestDto request);

    void resetPassword(ResetPasswordRequestDto request);

}
