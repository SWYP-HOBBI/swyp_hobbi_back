package swyp.hobbi.swyphobbiback.post_image.domain;

import jakarta.persistence.*;
import lombok.*;
import swyp.hobbi.swyphobbiback.post.domain.Post;

@Entity
@Table(name = "post_image")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@ToString
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String imageFileName;

    @Column(nullable = false)
    private String imageUrl;
}
