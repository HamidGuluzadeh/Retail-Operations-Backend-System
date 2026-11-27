package com.retail.ops.staff.controller;

import com.retail.ops.common.dto.SuccessDto;
import com.retail.ops.common.model.SuccessStatus;
import com.retail.ops.staff.dto.request.CreateStoreRequestDto;
import com.retail.ops.staff.dto.request.UpdateStoreRequestDto;
import com.retail.ops.staff.dto.response.StoreResponseDto;
import com.retail.ops.staff.model.StoreFormat;
import com.retail.ops.staff.model.StoreStatus;
import com.retail.ops.staff.service.StoreService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StoreController {
    StoreService storeService;

    @GetMapping
    public ResponseEntity<SuccessDto<Page<StoreResponseDto>>> getStores(StoreFormat format, StoreStatus status, Pageable pageable) {
        Page<StoreResponseDto> response = storeService.getStores(format, status, pageable);
        SuccessDto<Page<StoreResponseDto>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<SuccessDto<StoreResponseDto>> getStoreById(@PathVariable String storeId) {
        StoreResponseDto response = storeService.getStoreById(storeId);
        SuccessDto<StoreResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<SuccessDto<StoreResponseDto>> getStoreByCode(@PathVariable String code) {
        StoreResponseDto response = storeService.getStoreByCode(code);
        SuccessDto<StoreResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SuccessDto<StoreResponseDto>> createStore(@RequestBody @Valid CreateStoreRequestDto request) {
        StoreResponseDto response = storeService.createStore(request);
        SuccessDto<StoreResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PutMapping("/{storeId}")
    public ResponseEntity<SuccessDto<StoreResponseDto>> updateStore(@PathVariable String storeId,
                                                                    @RequestBody @Valid UpdateStoreRequestDto request) {
        StoreResponseDto response = storeService.updateStore(storeId, request);
        SuccessDto<StoreResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PatchMapping("/{storeId}")
    public ResponseEntity<SuccessDto<StoreResponseDto>> updateStatus(@PathVariable String storeId,
                                                                     @RequestParam StoreStatus newStatus) {
        StoreResponseDto response = storeService.updateStatus(storeId, newStatus);
        SuccessDto<StoreResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }
}
