package swyp.hobbi.swyphobbiback.common.exception;

import lombok.Getter;
import swyp.hobbi.swyphobbiback.common.error.ErrorCode;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
