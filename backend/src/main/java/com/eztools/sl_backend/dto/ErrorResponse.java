package com.eztools.sl_backend.dto;

/**
 * @author <Rezven>
 * 错误响应封装
 * @param errorCode 错误类型代码
 * @param errorMessage 错误详细信息
 */
public record ErrorResponse(
        String errorCode,
        String errorMessage
) {}