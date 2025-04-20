package swyp.hobbi.swyphobbiback.common.error;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private ErrorCode errorCode;
    private List<FieldError> fieldErrors;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class FieldError {
        private String field;
        private String message;
    }
}
