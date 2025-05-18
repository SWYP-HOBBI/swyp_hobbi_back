package swyp.hobbi.swyphobbiback.common.error;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 4xx
    INVALID_INPUT_VALUE(400, "INVALID_INPUT_VALUE", "요청 값이 유효하지 않습니다."),
    EMAIL_NOT_VERIFIED(400, "EMAIL_NOT_VERIFIED", "이메일 인증이 완료되지 않았습니다."),
    INVALID_TOKEN(400, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    USER_ALREADY_DELETED(400, "USER_ALREADY_DELETED", "이미 탈퇴한 사용자입니다."),
    EXPIRED_TOKEN(401, "EXPIRED_TOKEN", "토큰이 만료되었습니다."),
    PASSWORD_NOT_MATCH(401, "PASSWORD_NOT_MATCH", "비밀번호가 일치하지 않습니다."),
    UNAUTHORIZED(401, "UNAUTHORIZED","인증이 필요합니다."),
    FORBIDDEN(403, "FORBIDDEN","접근 권한이 없습니다."),
    USER_NOT_FOUND(404, "USER_NOT_FOUND","사용자를 찾을 수 없습니다."),
    POST_NOT_FOUND(404, "POST_NOT_FOUND","게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(404, "COMMENT_NOT_FOUND", "댓글을 찾을 수 없습니다."),
    IMAGE_NOT_FOUND(404, "IMAGE_NOT_FOUND", "이미지를 찾을 수 없습니다."),

    EMAIL_ALREADY_EXISTS(409, "EMAIL_ALREADY_EXISTS", "이미 존재하는 이메일입니다."),
    NICKNAME_ALREADY_EXISTS(409, "NICKNAME_ALREADY_EXISTS", "이미 존재하는 닉네임입니다."),

    // 5xx
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR","서버 에러가 발생했습니다."),
    FAILED_TO_UPLOAD_FILE(500, "FAILED_TO_UPLOAD_FILE", "파일 업로드에 실패하였습니다."),
    EXCEED_FILE_SIZE_LIMIT(500, "EXCEED_FILE_SIZE_LIMIT", "파일 용량이 초과하였습니다.");

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
