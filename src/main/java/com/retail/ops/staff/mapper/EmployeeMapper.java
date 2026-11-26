package com.retail.ops.staff.mapper;

import com.retail.ops.staff.dto.request.CreateEmployeeRequestDto;
import com.retail.ops.staff.dto.request.UpdateEmployeeRequestDto;
import com.retail.ops.staff.dto.response.EmployeeResponseDto;
import com.retail.ops.staff.entity.EmployeeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeeMapper {

    @Mapping(source = "store.id", target = "storeId")
    EmployeeResponseDto mapEntityToResponse(EmployeeEntity employeeEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "store", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    EmployeeEntity mapCreateRequestToEntity(CreateEmployeeRequestDto request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "store", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromRequest(UpdateEmployeeRequestDto request, @MappingTarget EmployeeEntity entity);

}
