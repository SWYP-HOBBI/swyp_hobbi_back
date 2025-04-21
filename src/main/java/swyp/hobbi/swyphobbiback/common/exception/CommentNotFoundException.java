package swyp.hobbi.swyphobbiback.common.exception;

import swyp.hobbi.swyphobbiback.common.error.ErrorCode;

public class CommentNotFoundException extends CustomException {
    public CommentNotFoundException() {
        super(ErrorCode.COMMENT_NOT_FOUND);
    }
}
