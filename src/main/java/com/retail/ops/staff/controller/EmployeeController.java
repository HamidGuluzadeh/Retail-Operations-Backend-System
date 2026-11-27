package com.retail.ops.staff.controller;

import com.retail.ops.common.dto.SuccessDto;
import com.retail.ops.common.model.SuccessStatus;
import com.retail.ops.staff.dto.request.CreateEmployeeRequestDto;
import com.retail.ops.staff.dto.request.UpdateEmployeeRequestDto;
import com.retail.ops.staff.dto.response.EmployeeResponseDto;
import com.retail.ops.staff.model.EmployeeRole;
import com.retail.ops.staff.model.EmployeeStatus;
import com.retail.ops.staff.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeController {
    EmployeeService employeeService;

    @GetMapping("/store/{storeId}")
    public ResponseEntity<SuccessDto<Page<EmployeeResponseDto>>> getEmployees(@PathVariable String storeId,
                                                                              @RequestParam(required = false) EmployeeRole role,
                                                                              @RequestParam(required = false) EmployeeStatus status,
                                                                              @PageableDefault(size = 20, sort = "firstName") Pageable pageable) {
        Page<EmployeeResponseDto> response = employeeService.getEmployees(storeId, role, status, pageable);
        SuccessDto<Page<EmployeeResponseDto>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<SuccessDto<EmployeeResponseDto>> getEmployeeById(@PathVariable String employeeId) {
        EmployeeResponseDto response = employeeService.getEmployeeById(employeeId);
        SuccessDto<EmployeeResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SuccessDto<EmployeeResponseDto>> createEmployee(@RequestBody @Valid CreateEmployeeRequestDto request) {
        EmployeeResponseDto response = employeeService.createEmployee(request);
        SuccessDto<EmployeeResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<SuccessDto<EmployeeResponseDto>> updateEmployee(@PathVariable String employeeId,
                                                                          @RequestBody @Valid UpdateEmployeeRequestDto request) {
        EmployeeResponseDto response = employeeService.updateEmployee(employeeId, request);
        SuccessDto<EmployeeResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PatchMapping("/{employeeId}/transfer")
    public ResponseEntity<SuccessDto<EmployeeResponseDto>> transferStore(@PathVariable String employeeId,
                                                                         @RequestParam String targetStoreId) {
        EmployeeResponseDto response = employeeService.transferStore(employeeId, targetStoreId);
        SuccessDto<EmployeeResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PatchMapping("/{employeeId}/role")
    public ResponseEntity<SuccessDto<EmployeeResponseDto>> updateRole(@PathVariable String employeeId,
                                                                      @RequestParam EmployeeRole newRole) {
        EmployeeResponseDto response = employeeService.updateRole(employeeId, newRole);
        SuccessDto<EmployeeResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PatchMapping("/{employeeId}/status")
    public ResponseEntity<SuccessDto<EmployeeResponseDto>> updateStatus(@PathVariable String employeeId,
                                                                        @RequestParam EmployeeStatus newStatus) {
        EmployeeResponseDto response = employeeService.updateStatus(employeeId, newStatus);
        SuccessDto<EmployeeResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }
}
