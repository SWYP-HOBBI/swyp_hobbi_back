package swyp.hobbi.swyphobbiback.common.exception;

import swyp.hobbi.swyphobbiback.common.error.ErrorCode;

public class ImageNotFoundException extends CustomException {
    public ImageNotFoundException() {
        super(ErrorCode.IMAGE_NOT_FOUND);
    }
}
