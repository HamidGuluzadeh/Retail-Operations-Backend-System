package com.retail.ops.staff.service.implement;

import com.retail.ops.common.exception.BadRequestException;
import com.retail.ops.common.exception.ConflictException;
import com.retail.ops.common.exception.ResourceAlreadyExistsException;
import com.retail.ops.common.exception.ResourceNotFoundException;
import com.retail.ops.staff.dto.request.CreateEmployeeRequestDto;
import com.retail.ops.staff.dto.request.UpdateEmployeeRequestDto;
import com.retail.ops.staff.dto.response.EmployeeResponseDto;
import com.retail.ops.staff.entity.EmployeeEntity;
import com.retail.ops.staff.entity.StoreEntity;
import com.retail.ops.staff.event.EmployeeDeactivatedEvent;
import com.retail.ops.staff.mapper.EmployeeMapper;
import com.retail.ops.staff.model.EmployeeRole;
import com.retail.ops.staff.model.EmployeeStatus;
import com.retail.ops.staff.model.StoreStatus;
import com.retail.ops.staff.repository.EmployeeRepository;
import com.retail.ops.staff.repository.StoreRepository;
import com.retail.ops.staff.service.EmployeeService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeServiceImpl implements EmployeeService {
    EmployeeRepository employeeRepository;
    StoreRepository storeRepository;
    EmployeeMapper employeeMapper;
    ApplicationEventPublisher eventPublisher;

    @Override
    public Page<EmployeeResponseDto> getEmployees(
            String storeId, EmployeeRole role, EmployeeStatus status, Pageable pageable) {
        return employeeRepository.findAllByFilters(storeId, role, status, pageable)
                .map(employeeMapper::mapEntityToResponse);
    }

    @Override
    public EmployeeResponseDto getEmployeeById(String employeeId) {
        EmployeeEntity employeeEntity = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("İşçi tapılmadı"));

        return employeeMapper.mapEntityToResponse(employeeEntity);
    }

    @Override
    @Transactional
    public EmployeeResponseDto createEmployee(CreateEmployeeRequestDto request) {
        if (employeeRepository.existsByPhoneNumber(request.phoneNumber().trim())) {
            throw new ResourceAlreadyExistsException("Bu telefon nömrəsi artıq qeydiyyatdadır");
        }

        StoreEntity storeEntity = storeRepository.findById(request.storeId())
                .orElseThrow(() -> new ResourceNotFoundException("Mağaza tapılmadı"));

        if (!storeEntity.getStatus().equals(StoreStatus.ACTIVE)) {
            throw new BadRequestException("Əməliyyat üçün mağaza ACTIVE statusunda olmalıdır");
        }

        EmployeeEntity employeeEntity = employeeMapper.mapCreateRequestToEntity(request);
        employeeEntity.setStore(storeEntity);
        employeeEntity.setStatus(EmployeeStatus.ACTIVE);
        employeeEntity.setCreatedAt(Instant.now());

        return employeeMapper.mapEntityToResponse(employeeEntity);
    }

    @Override
    @Transactional
    public EmployeeResponseDto updateEmployee(String employeeId, UpdateEmployeeRequestDto request) {
        EmployeeEntity employeeEntity = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("İşçi tapılmadı"));

        if (employeeEntity.getStatus().equals(EmployeeStatus.TERMINATED)) {
            throw new BadRequestException("İşdən çıxarılmış işçi üzərində əməliyyat aparıla bilməz");
        }

        if (employeeRepository.existsByPhoneNumberAndIdNot(request.phoneNumber(), employeeId)) {
            throw new ConflictException("Bu telefon nömrəsi başqa işçiyə aiddir");
        }

        employeeMapper.updateEntityFromRequest(request, employeeEntity);
        employeeEntity.setUpdatedAt(Instant.now());

        return employeeMapper.mapEntityToResponse(employeeEntity);
    }

    @Override
    @Transactional
    public EmployeeResponseDto transferStore(String employeeId, String targetStoreId) {
        EmployeeEntity employeeEntity = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("İşçi tapılmadı"));

        if (!employeeEntity.getStatus().equals(EmployeeStatus.ACTIVE)) {
            throw new BadRequestException("Yalnız ACTIVE statuslu işçilər üçün icra edilə bilər");
        }

        if (employeeEntity.getStore().getId().equals(targetStoreId)) {
            throw new ConflictException("İşçi artıq bu mağazada qeydiyyatdadır");
        }

        StoreEntity storeEntity = storeRepository.findById(targetStoreId)
                .orElseThrow(() -> new ResourceNotFoundException("Mağaza tapılmadı"));

        if (!storeEntity.getStatus().equals(StoreStatus.ACTIVE)) {
            throw new BadRequestException("Əməliyyat üçün mağaza ACTIVE statusunda olmalıdır");
        }

        employeeEntity.setStore(storeEntity);
        employeeEntity.setUpdatedAt(Instant.now());

        return employeeMapper.mapEntityToResponse(employeeEntity);
    }

    @Override
    @Transactional
    public EmployeeResponseDto updateRole(String employeeId, EmployeeRole newRole) {
        EmployeeEntity employeeEntity = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("İşçi tapılmadı"));

        if (!employeeEntity.getStatus().equals(EmployeeStatus.ACTIVE)) {
            throw new BadRequestException("Yalnız ACTIVE statuslu işçilər üçün icra edilə bilər");
        }

        if (employeeEntity.getRole().equals(newRole)) {
            throw new ConflictException("İşçi hal-hazırda bu vəzifədədir");
        }

        employeeEntity.setRole(newRole);
        employeeEntity.setUpdatedAt(Instant.now());

        return employeeMapper.mapEntityToResponse(employeeEntity);
    }

    @Override
    @Transactional
    public EmployeeResponseDto updateStatus(String employeeId, EmployeeStatus newStatus) {
        EmployeeEntity employeeEntity = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("İşçi tapılmadı"));

        if (employeeEntity.getStatus().equals(EmployeeStatus.TERMINATED)) {
            throw new BadRequestException("İşdən çıxarılmış işçi üzərində əməliyyat aparıla bilməz");
        }

        if (employeeEntity.getStatus().equals(newStatus)) {
            throw new ConflictException("İşçi hal-hazırda bu statusdadır");
        }

        employeeEntity.setStatus(newStatus);
        employeeEntity.setUpdatedAt(Instant.now());

        if (newStatus.equals(EmployeeStatus.TERMINATED) || newStatus.equals(EmployeeStatus.SUSPENDED)) {
            eventPublisher.publishEvent(new EmployeeDeactivatedEvent(
                    employeeEntity.getId(),
                    employeeEntity.getStore().getId(),
                    newStatus,
                    Instant.now()
            ));
        }

        return employeeMapper.mapEntityToResponse(employeeEntity);
    }
}
