package swyp.hobbi.swyphobbiback.challenge.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import swyp.hobbi.swyphobbiback.user.domain.User;

@Entity
@Table(name = "challenge")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Builder
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long challengeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private ChallengeType challengeType;

    private Double challengeBarPoint;

    private Boolean started;

    private Boolean achieved;
}
