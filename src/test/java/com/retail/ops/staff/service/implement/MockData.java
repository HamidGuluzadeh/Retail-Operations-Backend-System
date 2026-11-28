package com.retail.ops.staff.service.implement;

import com.retail.ops.staff.dto.request.CreateEmployeeRequestDto;
import com.retail.ops.staff.dto.request.CreateStoreRequestDto;
import com.retail.ops.staff.dto.request.UpdateEmployeeRequestDto;
import com.retail.ops.staff.dto.request.UpdateStoreRequestDto;
import com.retail.ops.staff.dto.response.EmployeeResponseDto;
import com.retail.ops.staff.dto.response.StoreResponseDto;
import com.retail.ops.staff.entity.EmployeeEntity;
import com.retail.ops.staff.entity.StoreEntity;
import com.retail.ops.staff.model.EmployeeRole;
import com.retail.ops.staff.model.EmployeeStatus;
import com.retail.ops.staff.model.StoreFormat;
import com.retail.ops.staff.model.StoreStatus;

import java.time.Instant;

public class MockData {
    public static final String STORE_ID = "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d";
    public static final String TARGET_STORE_ID = "b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e";
    public static final String EMPLOYEE_ID = "c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f";

    public static StoreEntity storeEntity() {
        StoreEntity store = new StoreEntity();
        store.setId(STORE_ID);
        store.setCode("STR001");
        store.setName("Baku Central Store");
        store.setFormat(StoreFormat.SUPERMARKET);
        store.setStatus(StoreStatus.ACTIVE);
        store.setCreatedAt(Instant.now());
        store.setCreatedBy("SYSTEM");
        return store;
    }

    public static StoreEntity targetStoreEntity() {
        StoreEntity store = new StoreEntity();
        store.setId(TARGET_STORE_ID);
        store.setCode("STR002");
        store.setName("Ganjlik Mall Store");
        store.setFormat(StoreFormat.HYPERMARKET);
        store.setStatus(StoreStatus.ACTIVE);
        store.setCreatedAt(Instant.now());
        store.setCreatedBy("SYSTEM");
        return store;
    }

    public static StoreEntity closedStoreEntity() {
        StoreEntity store = storeEntity();
        store.setStatus(StoreStatus.CLOSED);
        return store;
    }

    public static CreateStoreRequestDto createStoreRequestDto() {
        return new CreateStoreRequestDto(
                "str001",
                "Baku Central Store",
                StoreFormat.SUPERMARKET
        );
    }

    public static UpdateStoreRequestDto updateStoreRequestDto() {
        return new UpdateStoreRequestDto(
                "Updated Store Name",
                StoreFormat.HYPERMARKET
        );
    }

    public static StoreResponseDto storeResponseDto() {
        return new StoreResponseDto(
                STORE_ID,
                "STR001",
                "Baku Central Store",
                StoreFormat.SUPERMARKET,
                Instant.now(),
                "SYSTEM",
                null,
                null
        );
    }

    public static EmployeeEntity employeeEntity() {
        EmployeeEntity employee = new EmployeeEntity();
        employee.setId(EMPLOYEE_ID);
        employee.setStore(storeEntity());
        employee.setFirstName("Hamid");
        employee.setLastName("Guluzadeh");
        employee.setPhoneNumber("+994501234567");
        employee.setEmail("hamid@retail.ops");
        employee.setRole(EmployeeRole.CASHIER);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setCreatedAt(Instant.now());
        employee.setCreatedBy("SYSTEM");
        return employee;
    }

    public static EmployeeEntity suspendedEmployeeEntity() {
        EmployeeEntity employee = employeeEntity();
        employee.setStatus(EmployeeStatus.SUSPENDED);
        return employee;
    }

    public static EmployeeEntity terminatedEmployeeEntity() {
        EmployeeEntity employee = employeeEntity();
        employee.setStatus(EmployeeStatus.TERMINATED);
        return employee;
    }

    public static CreateEmployeeRequestDto createEmployeeRequestDto() {
        return new CreateEmployeeRequestDto(
                STORE_ID,
                "Hamid",
                "Guluzadeh",
                "+994501234567",
                "hamid@retail.ops",
                EmployeeRole.CASHIER
        );
    }

    public static UpdateEmployeeRequestDto updateEmployeeRequestDto() {
        return new UpdateEmployeeRequestDto(
                "Hamid",
                "Guluzadeh",
                "+994557654321",
                "hamid.updated@retail.ops"
        );
    }

    public static EmployeeResponseDto employeeResponseDto() {
        return new EmployeeResponseDto(
                EMPLOYEE_ID,
                STORE_ID,
                "Hamid",
                "Guluzadeh",
                "+994501234567",
                "hamid@retail.ops",
                EmployeeRole.CASHIER,
                EmployeeStatus.ACTIVE,
                Instant.now(),
                "SYSTEM",
                null,
                null
        );
    }
}
