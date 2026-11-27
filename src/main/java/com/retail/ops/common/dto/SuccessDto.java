package com.retail.ops.common.dto;

import com.retail.ops.common.model.SuccessStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SuccessDto<T> {
    String status;
    T data;

    public SuccessDto(SuccessStatus successStatus, T data) {
        this.status = successStatus.name();
        this.data = data;
    }
}
