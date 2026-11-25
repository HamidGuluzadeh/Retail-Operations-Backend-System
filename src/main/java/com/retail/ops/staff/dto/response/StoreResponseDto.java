package com.retail.ops.staff.dto.response;

import com.retail.ops.staff.model.StoreFormat;

import java.time.Instant;

public record StoreResponseDto(String id,
                               String code,
                               String name,
                               StoreFormat format,
                               Instant createdAt,
                               String createdBy,
                               Instant updatedAt,
                               String updatedBy) {

}
