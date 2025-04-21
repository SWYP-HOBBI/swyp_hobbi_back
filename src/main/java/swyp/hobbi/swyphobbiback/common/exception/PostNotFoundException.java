package swyp.hobbi.swyphobbiback.common.exception;

import swyp.hobbi.swyphobbiback.common.error.ErrorCode;

public class PostNotFoundException extends CustomException {
    public PostNotFoundException() {
        super(ErrorCode.POST_NOT_FOUND);
    }
}
