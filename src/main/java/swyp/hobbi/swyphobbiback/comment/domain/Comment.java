package swyp.hobbi.swyphobbiback.comment.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import swyp.hobbi.swyphobbiback.post.domain.Post;
import swyp.hobbi.swyphobbiback.user.domain.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_comment")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private Long parentCommentId;

    @Lob
    @Column(columnDefinition = "TEXT")
    @Size(max = 1000)
    private String commentContent;
    private Boolean deleted;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public boolean isRoot() {
        return parentCommentId == null;
    }

    public void setDeletedTrue() {
        deleted = true;
    }

    public void update(String commentContent) {
        this.commentContent = commentContent;
        this.updatedAt = LocalDateTime.now();
    }
}
