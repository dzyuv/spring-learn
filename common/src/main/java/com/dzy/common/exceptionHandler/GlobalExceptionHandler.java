package com.dzy.common.exceptionHandler;

import com.dzy.common.entity.ResultJSON;
import com.dzy.common.exception.BusinessException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResultJSON handleBusinessException(BusinessException e) {
        return ResultJSON.error(400, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultJSON handleValidationException(MethodArgumentNotValidException e) {
        // 提取字段错误信息
        String message = e.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResultJSON.error(400, message);
    }
}
