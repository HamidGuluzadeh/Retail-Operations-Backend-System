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
import com.retail.ops.staff.repository.EmployeeRepository;
import com.retail.ops.staff.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.retail.ops.staff.service.implement.MockData.EMPLOYEE_ID;
import static com.retail.ops.staff.service.implement.MockData.STORE_ID;
import static com.retail.ops.staff.service.implement.MockData.TARGET_STORE_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {
    @Mock
    EmployeeRepository employeeRepository;
    @Mock
    StoreRepository storeRepository;
    @Mock
    EmployeeMapper employeeMapper;
    @Mock
    ApplicationEventPublisher eventPublisher;

    @InjectMocks
    EmployeeServiceImpl employeeService;

    EmployeeEntity employeeEntity;
    EmployeeEntity suspendedEmployeeEntity;
    EmployeeEntity terminatedEmployeeEntity;
    StoreEntity storeEntity;
    StoreEntity targetStoreEntity;
    StoreEntity closedStoreEntity;
    CreateEmployeeRequestDto createEmployeeRequestDto;
    UpdateEmployeeRequestDto updateEmployeeRequestDto;
    EmployeeResponseDto employeeResponseDto;

    @BeforeEach
    void setUp() {
        employeeEntity = MockData.employeeEntity();
        suspendedEmployeeEntity = MockData.suspendedEmployeeEntity();
        terminatedEmployeeEntity = MockData.terminatedEmployeeEntity();
        storeEntity = MockData.storeEntity();
        targetStoreEntity = MockData.targetStoreEntity();
        closedStoreEntity = MockData.closedStoreEntity();
        createEmployeeRequestDto = MockData.createEmployeeRequestDto();
        updateEmployeeRequestDto = MockData.updateEmployeeRequestDto();
        employeeResponseDto = MockData.employeeResponseDto();
    }

    @Test
    void testWhenGetEmployeesSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<EmployeeEntity> entityPage = new PageImpl<>(List.of(employeeEntity), pageable, 1);

        when(employeeRepository.findAllByFilters(STORE_ID, EmployeeRole.CASHIER, EmployeeStatus.ACTIVE, pageable))
                .thenReturn(entityPage);
        when(employeeMapper.mapEntityToResponse(employeeEntity)).thenReturn(employeeResponseDto);

        Page<EmployeeResponseDto> result = employeeService.getEmployees(
                STORE_ID, EmployeeRole.CASHIER, EmployeeStatus.ACTIVE, pageable
        );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(employeeResponseDto, result.getContent().getFirst());

        verify(employeeRepository).findAllByFilters(STORE_ID, EmployeeRole.CASHIER, EmployeeStatus.ACTIVE, pageable);
        verify(employeeMapper).mapEntityToResponse(employeeEntity);
    }

    @Test
    void testWhenGetEmployeeByIdSuccess() {
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employeeEntity));
        when(employeeMapper.mapEntityToResponse(employeeEntity)).thenReturn(employeeResponseDto);

        EmployeeResponseDto result = employeeService.getEmployeeById(EMPLOYEE_ID);

        assertNotNull(result);
        assertEquals(employeeResponseDto.id(), result.id());

        verify(employeeRepository).findById(EMPLOYEE_ID);
        verify(employeeMapper).mapEntityToResponse(employeeEntity);
    }

    @Test
    void testWhenEmployeeNotFoundForGetById() {
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeById(EMPLOYEE_ID));

        verify(employeeRepository).findById(EMPLOYEE_ID);
        verifyNoInteractions(employeeMapper);
    }

    @Test
    void testWhenCreateEmployeeSuccess() {
        when(employeeRepository.existsByPhoneNumber("+994501234567")).thenReturn(false);
        when(storeRepository.findById(STORE_ID)).thenReturn(Optional.of(storeEntity));
        when(employeeMapper.mapCreateRequestToEntity(createEmployeeRequestDto)).thenReturn(employeeEntity);
        when(employeeMapper.mapEntityToResponse(employeeEntity)).thenReturn(employeeResponseDto);

        EmployeeResponseDto result = employeeService.createEmployee(createEmployeeRequestDto);

        assertNotNull(result);
        assertEquals(storeEntity, employeeEntity.getStore());
        assertEquals(EmployeeStatus.ACTIVE, employeeEntity.getStatus());
        assertEquals(employeeResponseDto, result);

        verify(employeeRepository).existsByPhoneNumber("+994501234567");
        verify(storeRepository).findById(STORE_ID);
        verify(employeeMapper).mapCreateRequestToEntity(createEmployeeRequestDto);
        verify(employeeMapper).mapEntityToResponse(employeeEntity);
    }

    @Test
    void testWhenPhoneNumberAlreadyExistsForCreate() {
        when(employeeRepository.existsByPhoneNumber("+994501234567")).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> employeeService.createEmployee(createEmployeeRequestDto));

        verify(employeeRepository).existsByPhoneNumber("+994501234567");
        verifyNoInteractions(storeRepository, employeeMapper);
    }

    @Test
    void testWhenStoreNotFoundForCreateEmployee() {
        when(employeeRepository.existsByPhoneNumber("+994501234567")).thenReturn(false);
        when(storeRepository.findById(STORE_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.createEmployee(createEmployeeRequestDto));

        verify(employeeRepository).existsByPhoneNumber("+994501234567");
        verify(storeRepository).findById(STORE_ID);
        verifyNoInteractions(employeeMapper);
    }

    @Test
    void testWhenStoreIsNotActiveForCreateEmployee() {
        when(employeeRepository.existsByPhoneNumber("+994501234567")).thenReturn(false);
        when(storeRepository.findById(STORE_ID)).thenReturn(Optional.of(closedStoreEntity));

        assertThrows(BadRequestException.class, () -> employeeService.createEmployee(createEmployeeRequestDto));

        verify(employeeRepository).existsByPhoneNumber("+994501234567");
        verify(storeRepository).findById(STORE_ID);
        verifyNoInteractions(employeeMapper);
    }

    @Test
    void testWhenUpdateEmployeeSuccess() {
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employeeEntity));
        when(employeeRepository.existsByPhoneNumberAndIdNot(updateEmployeeRequestDto.phoneNumber(), EMPLOYEE_ID))
                .thenReturn(false);
        when(employeeMapper.mapEntityToResponse(employeeEntity)).thenReturn(employeeResponseDto);

        EmployeeResponseDto result = employeeService.updateEmployee(EMPLOYEE_ID, updateEmployeeRequestDto);

        assertNotNull(result);
        assertEquals(employeeResponseDto, result);

        verify(employeeRepository).findById(EMPLOYEE_ID);
        verify(employeeRepository).existsByPhoneNumberAndIdNot(updateEmployeeRequestDto.phoneNumber(), EMPLOYEE_ID);
        verify(employeeMapper).updateEntityFromRequest(updateEmployeeRequestDto, employeeEntity);
        verify(employeeMapper).mapEntityToResponse(employeeEntity);
    }

    @Test
    void testWhenEmployeeNotFoundForUpdate() {
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> employeeService.updateEmployee(EMPLOYEE_ID, updateEmployeeRequestDto));

        verify(employeeRepository).findById(EMPLOYEE_ID);
        verifyNoInteractions(employeeMapper);
    }

    @Test
    void testWhenEmployeeIsTerminatedForUpdate() {
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(terminatedEmployeeEntity));

        assertThrows(BadRequestException.class,
                () -> employeeService.updateEmployee(EMPLOYEE_ID, updateEmployeeRequestDto));

        verify(employeeRepository).findById(EMPLOYEE_ID);
        verifyNoInteractions(employeeMapper);
    }

    @Test
    void testWhenPhoneNumberBelongsToAnotherEmployeeForUpdate() {
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employeeEntity));
        when(employeeRepository.existsByPhoneNumberAndIdNot(updateEmployeeRequestDto.phoneNumber(), EMPLOYEE_ID))
                .thenReturn(true);

        assertThrows(ConflictException.class,
                () -> employeeService.updateEmployee(EMPLOYEE_ID, updateEmployeeRequestDto));

        verify(employeeRepository).findById(EMPLOYEE_ID);
        verify(employeeRepository).existsByPhoneNumberAndIdNot(updateEmployeeRequestDto.phoneNumber(), EMPLOYEE_ID);
        verifyNoInteractions(employeeMapper);
    }

    @Test
    void testWhenTransferStoreSuccess() {
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employeeEntity));
        when(storeRepository.findById(TARGET_STORE_ID)).thenReturn(Optional.of(targetStoreEntity));
        when(employeeMapper.mapEntityToResponse(employeeEntity)).thenReturn(employeeResponseDto);

        EmployeeResponseDto result = employeeService.transferStore(EMPLOYEE_ID, TARGET_STORE_ID);

        assertNotNull(result);
        assertEquals(targetStoreEntity, employeeEntity.getStore());

        verify(employeeRepository).findById(EMPLOYEE_ID);
        verify(storeRepository).findById(TARGET_STORE_ID);
        verify(employeeMapper).mapEntityToResponse(employeeEntity);
    }

    @Test
    void testWhenEmployeeNotFoundForTransferStore() {
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.transferStore(EMPLOYEE_ID, TARGET_STORE_ID));

        verify(employeeRepository).findById(EMPLOYEE_ID);
        verifyNoInteractions(storeRepository, employeeMapper);
    }

    @Test
    void testWhenEmployeeIsNotActiveForTransferStore() {
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(suspendedEmployeeEntity));

        assertThrows(BadRequestException.class, () -> employeeService.transferStore(EMPLOYEE_ID, TARGET_STORE_ID));

        verify(employeeRepository).findById(EMPLOYEE_ID);
        verifyNoInteractions(storeRepository, employeeMapper);
    }

    @Test
    void testWhenTargetStoreIsSameAsCurrentStoreForTransfer() {
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employeeEntity));

        assertThrows(ConflictException.class, () -> employeeService.transferStore(EMPLOYEE_ID, STORE_ID));

        verify(employeeRepository).findById(EMPLOYEE_ID);
        verifyNoInteractions(storeRepository, employeeMapper);
    }

    @Test
    void testWhenTargetStoreNotFoundForTransfer() {
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employeeEntity));
        when(storeRepository.findById(TARGET_STORE_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.transferStore(EMPLOYEE_ID, TARGET_STORE_ID));

        verify(employeeRepository).findById(EMPLOYEE_ID);
        verify(storeRepository).findById(TARGET_STORE_ID);
        verifyNoInteractions(employeeMapper);
    }

    @Test
    void testWhenTargetStoreIsNotActiveForTransfer() {
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employeeEntity));
        when(storeRepository.findById(TARGET_STORE_ID)).thenReturn(Optional.of(closedStoreEntity));

        assertThrows(BadRequestException.class, () -> employeeService.transferStore(EMPLOYEE_ID, TARGET_STORE_ID));

        verify(employeeRepository).findById(EMPLOYEE_ID);
        verify(storeRepository).findById(TARGET_STORE_ID);
        verifyNoInteractions(employeeMapper);
    }

    @Test
    void testWhenUpdateRoleSuccess() {
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employeeEntity));
        when(employeeMapper.mapEntityToResponse(employeeEntity)).thenReturn(employeeResponseDto);

        EmployeeResponseDto result = employeeService.updateRole(EMPLOYEE_ID, EmployeeRole.STORE_MANAGER);

        assertNotNull(result);
        assertEquals(EmployeeRole.STORE_MANAGER, employeeEntity.getRole());

        verify(employeeRepository).findById(EMPLOYEE_ID);
        verify(employeeMapper).mapEntityToResponse(employeeEntity);
    }

    @Test
    void testWhenEmployeeNotFoundForUpdateRole() {
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> employeeService.updateRole(EMPLOYEE_ID, EmployeeRole.STORE_MANAGER));

        verify(employeeRepository).findById(EMPLOYEE_ID);
        verifyNoInteractions(employeeMapper);
    }

    @Test
    void testWhenEmployeeIsNotActiveForUpdateRole() {
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(suspendedEmployeeEntity));

        assertThrows(BadRequestException.class,
                () -> employeeService.updateRole(EMPLOYEE_ID, EmployeeRole.STORE_MANAGER));

        verify(employeeRepository).findById(EMPLOYEE_ID);
        verifyNoInteractions(employeeMapper);
    }

    @Test
    void testWhenEmployeeAlreadyHasSameRoleForUpdateRole() {
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employeeEntity));

        assertThrows(ConflictException.class,
                () -> employeeService.updateRole(EMPLOYEE_ID, EmployeeRole.CASHIER));

        verify(employeeRepository).findById(EMPLOYEE_ID);
        verifyNoInteractions(employeeMapper);
    }

    @Test
    void testWhenUpdateStatusFromSuspendedToActiveSuccess() {
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(suspendedEmployeeEntity));
        when(employeeMapper.mapEntityToResponse(suspendedEmployeeEntity)).thenReturn(employeeResponseDto);

        EmployeeResponseDto result = employeeService.updateStatus(EMPLOYEE_ID, EmployeeStatus.ACTIVE);

        assertNotNull(result);
        assertEquals(EmployeeStatus.ACTIVE, suspendedEmployeeEntity.getStatus());

        verify(employeeRepository).findById(EMPLOYEE_ID);
        verify(employeeMapper).mapEntityToResponse(suspendedEmployeeEntity);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void testWhenUpdateStatusToSuspendedPublishesEvent() {
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employeeEntity));
        when(employeeMapper.mapEntityToResponse(employeeEntity)).thenReturn(employeeResponseDto);

        EmployeeResponseDto result = employeeService.updateStatus(EMPLOYEE_ID, EmployeeStatus.SUSPENDED);

        assertNotNull(result);
        assertEquals(EmployeeStatus.SUSPENDED, employeeEntity.getStatus());

        verify(employeeRepository).findById(EMPLOYEE_ID);
        verify(eventPublisher).publishEvent(any(EmployeeDeactivatedEvent.class));
        verify(employeeMapper).mapEntityToResponse(employeeEntity);
    }

    @Test
    void testWhenUpdateStatusToTerminatedPublishesEvent() {
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employeeEntity));
        when(employeeMapper.mapEntityToResponse(employeeEntity)).thenReturn(employeeResponseDto);

        EmployeeResponseDto result = employeeService.updateStatus(EMPLOYEE_ID, EmployeeStatus.TERMINATED);

        assertNotNull(result);
        assertEquals(EmployeeStatus.TERMINATED, employeeEntity.getStatus());

        verify(employeeRepository).findById(EMPLOYEE_ID);
        verify(eventPublisher).publishEvent(any(EmployeeDeactivatedEvent.class));
        verify(employeeMapper).mapEntityToResponse(employeeEntity);
    }

    @Test
    void testWhenEmployeeNotFoundForUpdateStatus() {
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> employeeService.updateStatus(EMPLOYEE_ID, EmployeeStatus.TERMINATED));

        verify(employeeRepository).findById(EMPLOYEE_ID);
        verifyNoInteractions(eventPublisher, employeeMapper);
    }

    @Test
    void testWhenEmployeeIsTerminatedForUpdateStatus() {
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(terminatedEmployeeEntity));

        assertThrows(BadRequestException.class,
                () -> employeeService.updateStatus(EMPLOYEE_ID, EmployeeStatus.ACTIVE));

        verify(employeeRepository).findById(EMPLOYEE_ID);
        verifyNoInteractions(eventPublisher, employeeMapper);
    }

    @Test
    void testWhenEmployeeAlreadyHasSameStatusForUpdateStatus() {
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employeeEntity));

        assertThrows(ConflictException.class,
                () -> employeeService.updateStatus(EMPLOYEE_ID, EmployeeStatus.ACTIVE));

        verify(employeeRepository).findById(EMPLOYEE_ID);
        verifyNoInteractions(eventPublisher, employeeMapper);
    }
}