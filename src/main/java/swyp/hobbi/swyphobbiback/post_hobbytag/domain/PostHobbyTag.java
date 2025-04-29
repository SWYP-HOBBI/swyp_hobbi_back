package swyp.hobbi.swyphobbiback.post_hobbytag.domain;

import jakarta.persistence.*;
import lombok.*;
import swyp.hobbi.swyphobbiback.hobbytag.domain.HobbyTag;
import swyp.hobbi.swyphobbiback.post.domain.Post;

@Entity
@Table(name = "post_hobby_tag")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class PostHobbyTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postHobbyTagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hobby_tag_id", nullable = false)
    private HobbyTag hobbyTag;
}
