package com.retail.ops.auth.dto.response;

import com.retail.ops.staff.model.EmployeeRole;
import com.retail.ops.staff.model.EmployeeStatus;

import java.time.Instant;

public record UserResponseDto(String id,
                              String employeeId,
                              String username,
                              String firstName,
                              String lastName,
                              EmployeeRole role,
                              EmployeeStatus status,
                              Instant createdAt,
                              String createdBy,
                              Instant updatedAt,
                              String updatedBy) {

}
