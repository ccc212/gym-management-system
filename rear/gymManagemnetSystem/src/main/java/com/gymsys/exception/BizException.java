package com.gymsys.exception;

import com.gymsys.enums.StatusCodeEnum;
import lombok.Getter;

@Getter
public class BizException extends RuntimeException {
    private final StatusCodeEnum statusCodeEnum;

    public BizException(String message) {
        super(message);
        this.statusCodeEnum = StatusCodeEnum.FAIL;
    }

    public BizException(StatusCodeEnum statusCodeEnum) {
        super(statusCodeEnum.getDesc());
        this.statusCodeEnum = statusCodeEnum;
    }
}
