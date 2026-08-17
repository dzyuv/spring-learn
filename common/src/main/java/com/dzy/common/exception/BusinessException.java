package com.dzy.common.exception;

public class BusinessException extends RuntimeException {

    // 错误码，可用于前端区分不同错误
    private Integer code;

    // 错误消息
    private String message;

    /**
     * 只传错误消息，默认错误码 400
     */
    public BusinessException(String message) {
        super(message);
        this.code = 400;
        this.message = message;
    }

    /**
     * 同时传错误码和消息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}