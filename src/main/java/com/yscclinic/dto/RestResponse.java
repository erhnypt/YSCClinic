package com.yscclinic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestResponse<T> {

    private boolean success;
    private String message;
    private T data;

    public static <T> RestResponse<T> success(T data) {
        return RestResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> RestResponse<T> success(String message, T data) {
        return RestResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static RestResponse<?> error(String message) {
        return RestResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}
