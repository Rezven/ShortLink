package com.eztools.sl_backend.config;

import com.eztools.sl_backend.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


/**
 * @author <Rezven>
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    // 处理业务自定义异常
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(
            BusinessException ex
    ) {
        return ResponseEntity.status(ex.getCode())
                .body(buildErrorResponse(ex.getCode(), ex.getMessage()));
    }
    
    // 处理参数校验失败异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationExceptions(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return buildErrorResponse(400, "参数校验失败", errors);
    }
    
    // 处理404异常
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(
            NoHandlerFoundException ex
    ) {
        return buildErrorResponse(404, "接口不存在: " + ex.getRequestURL());
    }
    
    // 其他未处理异常兜底
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleOtherExceptions(Exception ex) {
        return buildErrorResponse(500, "系统繁忙，请稍后重试");
    }
    
    // 构造统一错误响应
    private Map<String, Object> buildErrorResponse(int code, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("code", code);
        body.put("message", message);
        return body;
    }
    
    private Map<String, Object> buildErrorResponse(
            int code, String message, Map<String, String> details
    ) {
        Map<String, Object> response = buildErrorResponse(code, message);
        response.put("errors", details);
        return response;
    }
}