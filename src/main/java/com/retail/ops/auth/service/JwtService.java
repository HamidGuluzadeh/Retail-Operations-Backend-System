package com.retail.ops.auth.service;

import com.retail.ops.auth.entity.UserEntity;

import java.security.Key;

public interface JwtService {

    String generateAccessToken(UserEntity userEntity);

    String generateRefreshToken(UserEntity userEntity);

    String hashToken(String tokenValue);

    Key getSigningKey();

}
