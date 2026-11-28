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
import com.retail.ops.staff.service.StoreService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StoreServiceImpl implements StoreService {
    StoreRepository storeRepository;
    StoreMapper storeMapper;
    EmployeeRepository employeeRepository;

    @Override
    public Page<StoreResponseDto> getStores(StoreFormat format, StoreStatus status, Pageable pageable) {
        return storeRepository.findAllByFilters(format, status, pageable)
                .map(storeMapper::mapEntityToResponse);
    }

    @Override
    public StoreResponseDto getStoreById(String storeId) {
        StoreEntity storeEntity = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Mağaza tapılmadı"));

        return storeMapper.mapEntityToResponse(storeEntity);
    }

    @Override
    public StoreResponseDto getStoreByCode(String code) {
        StoreEntity storeEntity =storeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Mağaza " + code + " tapılmadı"));

        return storeMapper.mapEntityToResponse(storeEntity);
    }

    @Override
    @Transactional
    public StoreResponseDto createStore(CreateStoreRequestDto request) {
        if (storeRepository.existsByCode(request.code().trim().toUpperCase())) {
            throw new ResourceAlreadyExistsException("Mağaza " + request.code().trim().toUpperCase() + " artıq mövcuddur!");
        }

        StoreEntity storeEntity = storeMapper.mapCreateRequestToEntity(request);
        storeEntity.setCode(request.code().trim().toUpperCase());
        storeEntity.setStatus(StoreStatus.ACTIVE);
        storeEntity.setCreatedAt(Instant.now());

        return storeMapper.mapEntityToResponse(storeEntity);
    }

    @Override
    @Transactional
    public StoreResponseDto updateStore(String storeId, UpdateStoreRequestDto request) {
        StoreEntity storeEntity = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Mağaza tapılmadı"));

        if (storeEntity.getStatus().equals(StoreStatus.CLOSED)) {
            throw new BadRequestException("Bağlanmış mağazanın məlumatları dəyişdirilə bilməz");
        }

        storeMapper.updateEntityFromRequest(request, storeEntity);
        storeEntity.setUpdatedAt(Instant.now());

        return storeMapper.mapEntityToResponse(storeEntity);
    }

    @Override
    @Transactional
    public StoreResponseDto updateStatus(String storeId, StoreStatus newStatus) {
        StoreEntity storeEntity = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Mağaza tapılmadı"));

        if (storeEntity.getStatus().equals(StoreStatus.CLOSED)) {
            throw new BadRequestException("Bağlanmış mağaza yenidən aktivləşdirilə bilməz");
        }

        if (storeEntity.getStatus().equals(newStatus)) {
            throw new ResourceAlreadyExistsException("Mağaza artıq bu statusdadır: " + newStatus);
        }

        if (newStatus == StoreStatus.CLOSED
                && employeeRepository.existsByStoreIdAndStatusNot(storeId, EmployeeStatus.TERMINATED)) {
            throw new ConflictException("Mağazada aktiv və ya qeydiyyatda olan işçilər var! Əvvəlcə transfer və ya xitam verilməlidir");
        }

        storeEntity.setStatus(newStatus);
        storeEntity.setUpdatedAt(Instant.now());

        return storeMapper.mapEntityToResponse(storeEntity);
    }
}
