package swyp.hobbi.swyphobbiback.common.exception;

import swyp.hobbi.swyphobbiback.common.error.ErrorCode;

public class FileUploadFailedException extends CustomException {
    public FileUploadFailedException() {
        super(ErrorCode.FAILED_TO_UPLOAD_FILE);
    }
}
