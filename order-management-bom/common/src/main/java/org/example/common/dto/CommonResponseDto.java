package org.example.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommonResponseDto<T> {
    private boolean success;
    private String message;
    private T data;
}

