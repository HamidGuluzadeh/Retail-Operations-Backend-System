package com.retail.ops.staff.service;

import com.retail.ops.staff.dto.request.CreateStoreRequestDto;
import com.retail.ops.staff.dto.request.UpdateStoreRequestDto;
import com.retail.ops.staff.dto.response.StoreResponseDto;
import com.retail.ops.staff.model.StoreFormat;
import com.retail.ops.staff.model.StoreStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoreService {

    Page<StoreResponseDto> getStores(StoreFormat format, StoreStatus status, Pageable pageable);

    StoreResponseDto getStoreById(String storeId);

    StoreResponseDto getStoreByCode(String code);

    StoreResponseDto createStore(CreateStoreRequestDto request);

    StoreResponseDto updateStore(String storeId, UpdateStoreRequestDto request);

    StoreResponseDto updateStatus(String storeId, StoreStatus newStatus);

}
