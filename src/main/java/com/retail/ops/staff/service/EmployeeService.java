package com.retail.ops.staff.service;

import com.retail.ops.staff.dto.request.CreateEmployeeRequestDto;
import com.retail.ops.staff.dto.request.UpdateEmployeeRequestDto;
import com.retail.ops.staff.dto.response.EmployeeResponseDto;
import com.retail.ops.staff.model.EmployeeRole;
import com.retail.ops.staff.model.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {

    Page<EmployeeResponseDto> getEmployees(String storeId, EmployeeRole role, EmployeeStatus status, Pageable pageable);

    EmployeeResponseDto getEmployeeById(String employeeId);

    EmployeeResponseDto createEmployee(CreateEmployeeRequestDto request);

    EmployeeResponseDto updateEmployee(String employeeId, UpdateEmployeeRequestDto request);

    EmployeeResponseDto transferStore(String employeeId, String targetStoreId);

    EmployeeResponseDto updateRole(String employeeId, EmployeeRole newRole);

    EmployeeResponseDto updateStatus(String employeeId, EmployeeStatus newStatus);

}
