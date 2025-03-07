package com.eztools.sl_backend.exception;

/**
 * 自定义业务异常类
 * @author <Rezven>
 */
public class BusinessException extends RuntimeException {
    private final int code;
    
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }
}