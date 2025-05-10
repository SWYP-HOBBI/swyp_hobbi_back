package swyp.hobbi.swyphobbiback.like.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyp.hobbi.swyphobbiback.common.error.ErrorCode;
import swyp.hobbi.swyphobbiback.common.exception.PostNotFoundException;
import swyp.hobbi.swyphobbiback.common.security.CustomUserDetails;
import swyp.hobbi.swyphobbiback.like.domain.Like;
import swyp.hobbi.swyphobbiback.like.domain.LikeCount;
import swyp.hobbi.swyphobbiback.like.dto.LikeResponse;
import swyp.hobbi.swyphobbiback.like.repository.LikeCountRepository;
import swyp.hobbi.swyphobbiback.like.repository.LikeRepository;
import swyp.hobbi.swyphobbiback.post.domain.Post;
import swyp.hobbi.swyphobbiback.post.repository.PostRepository;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final LikeCountRepository likeCountRepository;
    private final PostRepository postRepository;

    @Transactional
    public LikeResponse likeOptimisticLock(CustomUserDetails userDetails, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        if(userDetails == null || userDetails.getUsername() == null) {
            throw new AccessDeniedException(ErrorCode.FORBIDDEN.getMessage());
        }

        Like like = Like.builder()
                .user(userDetails.getUser())
                .post(post)
                .likeYn(true)
                .build();

        likeRepository.save(like);

        LikeCount likeCount = likeCountRepository.findById(postId)
                .orElseGet(() -> LikeCount.init(postId, 0L));
        likeCount.increase();
        likeCountRepository.save(likeCount);

        return LikeResponse.from(like);
    }

    @Transactional
    public LikeResponse unlikeOptimisticLock(CustomUserDetails userDetails, Long postId) {
        if(userDetails == null || userDetails.getUsername() == null) {
            throw new AccessDeniedException(ErrorCode.FORBIDDEN.getMessage());
        }

        Like like = likeRepository.findByPost_PostIdAndUser_UserId(postId, userDetails.getUserId())
                .orElseThrow();
        like.setLikeYnFalse();

        LikeCount likeCount = likeCountRepository.findById(postId)
                .orElseThrow();
        likeCount.decrease();

        return LikeResponse.from(like);
    }

    public Long getCount(Long postId) {
        return likeCountRepository.findById(postId)
                .map(LikeCount::getLikeCount)
                .orElse(0L);
    }
}
