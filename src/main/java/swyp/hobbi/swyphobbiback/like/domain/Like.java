package swyp.hobbi.swyphobbiback.like.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import swyp.hobbi.swyphobbiback.post.domain.Post;
import swyp.hobbi.swyphobbiback.user.domain.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_like")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Builder
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postLikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private Boolean likeYn;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public void setLikeYnTrue() {
        likeYn = true;
    }

    public void setLikeYnFalse() {
        likeYn = false;
    }
}
