package com.retail.ops.staff.service.implement;

import com.retail.ops.common.exception.BadRequestException;
import com.retail.ops.common.exception.ConflictException;
import com.retail.ops.common.exception.ResourceAlreadyExistsException;
import com.retail.ops.common.exception.ResourceNotFoundException;
import com.retail.ops.staff.dto.request.CreateStoreRequestDto;
import com.retail.ops.staff.dto.request.UpdateStoreRequestDto;
import com.retail.ops.staff.dto.response.StoreResponseDto;
import com.retail.ops.staff.entity.StoreEntity;
import com.retail.ops.staff.mapper.StoreMapper;
import com.retail.ops.staff.model.EmployeeStatus;
import com.retail.ops.staff.model.StoreFormat;
import com.retail.ops.staff.model.StoreStatus;
import com.retail.ops.staff.repository.EmployeeRepository;
import com.retail.ops.staff.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.retail.ops.staff.service.implement.MockData.STORE_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceImplTest {
    @Mock
    StoreRepository storeRepository;
    @Mock
    StoreMapper storeMapper;
    @Mock
    EmployeeRepository employeeRepository;

    @InjectMocks
    StoreServiceImpl storeService;

    StoreEntity storeEntity;
    StoreEntity closedStoreEntity;
    CreateStoreRequestDto createStoreRequestDto;
    UpdateStoreRequestDto updateStoreRequestDto;
    StoreResponseDto storeResponseDto;

    @BeforeEach
    void setUp() {
        storeEntity = MockData.storeEntity();
        closedStoreEntity = MockData.closedStoreEntity();
        createStoreRequestDto = MockData.createStoreRequestDto();
        updateStoreRequestDto = MockData.updateStoreRequestDto();
        storeResponseDto = MockData.storeResponseDto();
    }

    @Test
    void testWhenGetStoresSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<StoreEntity> entityPage = new PageImpl<>(List.of(storeEntity), pageable, 1);

        when(storeRepository.findAllByFilters(StoreFormat.SUPERMARKET, StoreStatus.ACTIVE, pageable))
                .thenReturn(entityPage);
        when(storeMapper.mapEntityToResponse(storeEntity)).thenReturn(storeResponseDto);

        Page<StoreResponseDto> result = storeService.getStores(StoreFormat.SUPERMARKET, StoreStatus.ACTIVE, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(storeResponseDto, result.getContent().getFirst());

        verify(storeRepository).findAllByFilters(StoreFormat.SUPERMARKET, StoreStatus.ACTIVE, pageable);
        verify(storeMapper).mapEntityToResponse(storeEntity);
    }

    @Test
    void testWhenGetStoreByIdSuccess() {
        when(storeRepository.findById(STORE_ID)).thenReturn(Optional.of(storeEntity));
        when(storeMapper.mapEntityToResponse(storeEntity)).thenReturn(storeResponseDto);

        StoreResponseDto result = storeService.getStoreById(STORE_ID);

        assertNotNull(result);
        assertEquals(storeResponseDto.id(), result.id());
        assertEquals(storeResponseDto.code(), result.code());

        verify(storeRepository).findById(STORE_ID);
        verify(storeMapper).mapEntityToResponse(storeEntity);
    }

    @Test
    void testWhenStoreNotFoundForGetById() {
        when(storeRepository.findById(STORE_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> storeService.getStoreById(STORE_ID));

        verify(storeRepository).findById(STORE_ID);
        verifyNoInteractions(storeMapper);
    }

    @Test
    void testWhenGetStoreByCodeSuccess() {
        when(storeRepository.findByCode("STR001")).thenReturn(Optional.of(storeEntity));
        when(storeMapper.mapEntityToResponse(storeEntity)).thenReturn(storeResponseDto);

        StoreResponseDto result = storeService.getStoreByCode("STR001");

        assertNotNull(result);
        assertEquals("STR001", result.code());

        verify(storeRepository).findByCode("STR001");
        verify(storeMapper).mapEntityToResponse(storeEntity);
    }

    @Test
    void testWhenStoreNotFoundForGetByCode() {
        when(storeRepository.findByCode("STR001")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> storeService.getStoreByCode("STR001"));

        verify(storeRepository).findByCode("STR001");
        verifyNoInteractions(storeMapper);
    }

    @Test
    void testWhenCreateStoreSuccess() {
        when(storeRepository.existsByCode("STR001")).thenReturn(false);
        when(storeMapper.mapCreateRequestToEntity(createStoreRequestDto)).thenReturn(storeEntity);
        when(storeMapper.mapEntityToResponse(storeEntity)).thenReturn(storeResponseDto);

        StoreResponseDto result = storeService.createStore(createStoreRequestDto);

        assertNotNull(result);
        assertEquals("STR001", storeEntity.getCode());
        assertEquals(StoreStatus.ACTIVE, storeEntity.getStatus());
        assertEquals(storeResponseDto, result);

        verify(storeRepository).existsByCode("STR001");
        verify(storeMapper).mapCreateRequestToEntity(createStoreRequestDto);
        verify(storeMapper).mapEntityToResponse(storeEntity);
    }

    @Test
    void testWhenStoreCodeAlreadyExistsForCreate() {
        when(storeRepository.existsByCode("STR001")).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> storeService.createStore(createStoreRequestDto));

        verify(storeRepository).existsByCode("STR001");
        verifyNoInteractions(storeMapper);
    }

    @Test
    void testWhenUpdateStoreSuccess() {
        when(storeRepository.findById(STORE_ID)).thenReturn(Optional.of(storeEntity));
        when(storeMapper.mapEntityToResponse(storeEntity)).thenReturn(storeResponseDto);

        StoreResponseDto result = storeService.updateStore(STORE_ID, updateStoreRequestDto);

        assertNotNull(result);
        assertEquals(storeResponseDto, result);

        verify(storeRepository).findById(STORE_ID);
        verify(storeMapper).updateEntityFromRequest(updateStoreRequestDto, storeEntity);
        verify(storeMapper).mapEntityToResponse(storeEntity);
    }

    @Test
    void testWhenStoreNotFoundForUpdate() {
        when(storeRepository.findById(STORE_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> storeService.updateStore(STORE_ID, updateStoreRequestDto));

        verify(storeRepository).findById(STORE_ID);
        verifyNoInteractions(storeMapper);
    }

    @Test
    void testWhenStoreIsClosedForUpdate() {
        when(storeRepository.findById(STORE_ID)).thenReturn(Optional.of(closedStoreEntity));

        assertThrows(BadRequestException.class, () -> storeService.updateStore(STORE_ID, updateStoreRequestDto));

        verify(storeRepository).findById(STORE_ID);
        verifyNoInteractions(storeMapper);
    }

    @Test
    void testWhenUpdateStatusSuccess() {
        when(storeRepository.findById(STORE_ID)).thenReturn(Optional.of(storeEntity));
        when(storeMapper.mapEntityToResponse(storeEntity)).thenReturn(storeResponseDto);

        StoreResponseDto result = storeService.updateStatus(STORE_ID, StoreStatus.RENOVATION);

        assertNotNull(result);
        assertEquals(StoreStatus.RENOVATION, storeEntity.getStatus());

        verify(storeRepository).findById(STORE_ID);
        verify(storeMapper).mapEntityToResponse(storeEntity);
    }

    @Test
    void testWhenUpdateStatusToClosedSuccess() {
        when(storeRepository.findById(STORE_ID)).thenReturn(Optional.of(storeEntity));
        when(employeeRepository.existsByStoreIdAndStatusNot(STORE_ID, EmployeeStatus.TERMINATED)).thenReturn(false);
        when(storeMapper.mapEntityToResponse(storeEntity)).thenReturn(storeResponseDto);

        StoreResponseDto result = storeService.updateStatus(STORE_ID, StoreStatus.CLOSED);

        assertNotNull(result);
        assertEquals(StoreStatus.CLOSED, storeEntity.getStatus());

        verify(storeRepository).findById(STORE_ID);
        verify(employeeRepository).existsByStoreIdAndStatusNot(STORE_ID, EmployeeStatus.TERMINATED);
        verify(storeMapper).mapEntityToResponse(storeEntity);
    }

    @Test
    void testWhenStoreNotFoundForUpdateStatus() {
        when(storeRepository.findById(STORE_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> storeService.updateStatus(STORE_ID, StoreStatus.CLOSED));

        verify(storeRepository).findById(STORE_ID);
        verifyNoInteractions(employeeRepository, storeMapper);
    }

    @Test
    void testWhenStoreIsClosedForUpdateStatus() {
        when(storeRepository.findById(STORE_ID)).thenReturn(Optional.of(closedStoreEntity));

        assertThrows(BadRequestException.class, () -> storeService.updateStatus(STORE_ID, StoreStatus.ACTIVE));

        verify(storeRepository).findById(STORE_ID);
        verifyNoInteractions(employeeRepository, storeMapper);
    }

    @Test
    void testWhenStoreAlreadyHasSameStatusForUpdateStatus() {
        when(storeRepository.findById(STORE_ID)).thenReturn(Optional.of(storeEntity));

        assertThrows(ResourceAlreadyExistsException.class, () -> storeService.updateStatus(STORE_ID, StoreStatus.ACTIVE));

        verify(storeRepository).findById(STORE_ID);
        verifyNoInteractions(employeeRepository, storeMapper);
    }

    @Test
    void testWhenStoreHasNonTerminatedEmployeesForClose() {
        when(storeRepository.findById(STORE_ID)).thenReturn(Optional.of(storeEntity));
        when(employeeRepository.existsByStoreIdAndStatusNot(STORE_ID, EmployeeStatus.TERMINATED)).thenReturn(true);

        assertThrows(ConflictException.class, () -> storeService.updateStatus(STORE_ID, StoreStatus.CLOSED));

        verify(storeRepository).findById(STORE_ID);
        verify(employeeRepository).existsByStoreIdAndStatusNot(STORE_ID, EmployeeStatus.TERMINATED);
        verifyNoInteractions(storeMapper);
    }
}