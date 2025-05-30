package swyp.hobbi.swyphobbiback.user.dto;

import lombok.Getter;

@Getter
public class UserDeleteRequest {
    private String reason; // 탈퇴 사유
}