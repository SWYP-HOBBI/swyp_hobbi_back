package swyp.hobbi.swyphobbiback.token.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    private String email;

    @Column(nullable = false)
    private String refreshToken;

}