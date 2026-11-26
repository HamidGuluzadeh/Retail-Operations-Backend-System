package com.retail.ops.staff.event;

import com.retail.ops.staff.model.EmployeeStatus;

import java.time.Instant;

public record EmployeeDeactivatedEvent(String employeeId,
                                       String storeId,
                                       EmployeeStatus newStatus,
                                       Instant occurredAt) {

}
