package com.eztools.sl_backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author <Rezven>
 * 统一API响应格式
 * @param <T> 数据泛型类型
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        int code,
        T data,
        String message
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, data, null);
    }
    
    public static ApiResponse<?> error(int code, String message) {
        return new ApiResponse<>(code, null, message);
    }
}