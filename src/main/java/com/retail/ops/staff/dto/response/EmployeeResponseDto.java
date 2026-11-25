package com.retail.ops.staff.dto.response;

import com.retail.ops.staff.model.EmployeeRole;
import com.retail.ops.staff.model.EmployeeStatus;

import java.time.Instant;

public record EmployeeResponseDto(String id,
                                  String storeId,
                                  String firstName,
                                  String lastName,
                                  String phoneNumber,
                                  String email,
                                  EmployeeRole role,
                                  EmployeeStatus status,
                                  Instant createdAt,
                                  String createdBy,
                                  Instant updatedAt,
                                  String updatedBy) {

}
