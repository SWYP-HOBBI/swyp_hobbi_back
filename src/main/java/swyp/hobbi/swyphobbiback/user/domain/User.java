package swyp.hobbi.swyphobbiback.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import swyp.hobbi.swyphobbiback.hobbytag.domain.HobbyTag;
import swyp.hobbi.swyphobbiback.user_hobbytag.domain.UserHobbyTag;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private Integer birthYear;

    @Column(nullable = false)
    private Integer birthMonth;

    @Column(nullable = false)
    private Integer birthDay;

    @Column(nullable = false)
    private String gender;

    private String mbti;

    @Column(nullable = false)
    private String userImageUrl;

    @Column(nullable = false)
    private String role;

    private Boolean isTagExist;

    private Boolean isBlocked;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserHobbyTag> userHobbyTags = new ArrayList<>();

    @Column(nullable = false)
    private Boolean isDeleted = false;  // 회원탈퇴 플래그

    // 기본 사용자 정보 수정
    public void updateProfile(String username, String gender, String mbti,
                              Integer birthYear,
                              Integer birthMonth, Integer birthDay) {
        this.username = username;
        this.gender = gender;
        this.mbti = mbti;
        this.birthYear = birthYear;
        this.birthMonth = birthMonth;
        this.birthDay = birthDay;
    }

    // 취미 태그 수정
    public void updateHobbyTags(List<HobbyTag> newTags) {
        this.userHobbyTags.clear();

        for (HobbyTag tag : newTags) {
            UserHobbyTag userHobbyTag = UserHobbyTag.builder()
                    .user(this)
                    .hobbyTag(tag)
                    .build();
            this.userHobbyTags.add(userHobbyTag);
        }

        this.isTagExist = !newTags.isEmpty();
    }

}
