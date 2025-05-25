package swyp.hobbi.swyphobbiback.challenge.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "challenge")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Builder
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long challengeId;

    private Long userId;

    private Integer challenge1Point;
    private Integer challenge2Point;
    private Integer challenge3Point;

    @Builder.Default
    private Boolean challenge1Started = false;
    @Builder.Default
    private Boolean challenge2Started = false;
    @Builder.Default
    private Boolean challenge3Started = false;

    @Builder.Default
    private Boolean challenge1Achieved = false;
    @Builder.Default
    private Boolean challenge2Achieved = false;
    @Builder.Default
    private Boolean challenge3Achieved = false;

    private LocalDateTime startedAt;
    private Long remainedSeconds;
}
