package com.eztools.sl_backend.exception;

/**
 * 404异常：资源不存在时抛出
 * @author <Rezven>
 */
public class NotFoundException extends BusinessException {
    public NotFoundException(String message) {
        super(404, message);
    }
}