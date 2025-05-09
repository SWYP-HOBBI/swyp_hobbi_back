package swyp.hobbi.swyphobbiback.like.domain;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "post_like_count")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Builder
public class LikeCount {
    @Id
    private Long postId;
    private Long likeCount;
    @Version
    private Long version;

    public static LikeCount init(Long postId, Long likeCount) {
        LikeCount count = new LikeCount();
        count.postId = postId;
        count.likeCount = likeCount;
        return count;
    }

    public void increase() {
        this.likeCount++;
    }

    public void decrease() {
        this.likeCount--;
    }
}
