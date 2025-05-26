package swyp.hobbi.swyphobbiback.user.domain;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "oauth_login")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
public class OauthLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String provider;  // 소셜 서비스

    @Column(nullable = false)
    private String providerEmail;
}