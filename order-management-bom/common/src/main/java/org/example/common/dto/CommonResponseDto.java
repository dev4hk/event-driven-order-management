package org.example.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponseDto<T> {

    private boolean success;
    private String message;
    private T data;

    public static <T> CommonResponseDto<T> success(String message, T data) {
        return CommonResponseDto.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> CommonResponseDto<T> success(String message) {
        return CommonResponseDto.<T>builder()
                .success(true)
                .message(message)
                .build();
    }

    public static <T> CommonResponseDto<T> failure(String message) {
        return CommonResponseDto.<T>builder()
                .success(false)
                .message(message)
                .build();
    }

    public static <T> CommonResponseDto<T> success(T data) {
        return CommonResponseDto.<T>builder()
                .success(true)
                .message("Success")
                .data(data)
                .build();
    }

}

