package swyp.hobbi.swyphobbiback.common.exception;

import swyp.hobbi.swyphobbiback.common.error.ErrorCode;

public class UserNotFoundException extends CustomException {
    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}
