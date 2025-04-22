package swyp.hobbi.swyphobbiback.common.error;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 4xx
    INVALID_INPUT_VALUE(400, "INVALID_INPUT_VALUE", "요청 값이 유효하지 않습니다."),
    UNAUTHORIZED(401, "UNAUTHORIZED","인증이 필요합니다."),
    FORBIDDEN(403, "FORBIDDEN","접근 권한이 없습니다."),
    USER_NOT_FOUND(404, "USER_NOT_FOUND","사용자를 찾을 수 없습니다."),
    POST_NOT_FOUND(404, "POST_NOT_FOUND","게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(404, "COMMENT_NOT_FOUND", "댓글을 찾을 수 없습니다."),

    // 5xx
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR","서버 에러가 발생했습니다.");

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
