package com.eztools.sl_backend.exception;

/**
 * 400异常：客户端请求参数错误时抛出
 * @author <Rezven>
 */
public class BadRequestException extends BusinessException {
    public BadRequestException(String message) {
        super(400, message);
    }
}