package swyp.hobbi.swyphobbiback.like.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swyp.hobbi.swyphobbiback.challenge.service.ChallengeService;
import swyp.hobbi.swyphobbiback.common.error.ErrorCode;
import swyp.hobbi.swyphobbiback.common.exception.PostNotFoundException;
import swyp.hobbi.swyphobbiback.common.security.CustomUserDetails;
import swyp.hobbi.swyphobbiback.like.domain.Like;
import swyp.hobbi.swyphobbiback.like.domain.LikeCount;
import swyp.hobbi.swyphobbiback.like.dto.LikeResponse;
import swyp.hobbi.swyphobbiback.like.repository.LikeCountRepository;
import swyp.hobbi.swyphobbiback.like.repository.LikeRepository;
import swyp.hobbi.swyphobbiback.notification.domain.NotificationType;
import swyp.hobbi.swyphobbiback.notification.service.NotificationService;
import swyp.hobbi.swyphobbiback.post.domain.Post;
import swyp.hobbi.swyphobbiback.post.repository.PostRepository;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final LikeCountRepository likeCountRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;
    private final ChallengeService challengeService;

    @Transactional
    public LikeResponse likeOptimisticLock(CustomUserDetails userDetails, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        if(userDetails == null || userDetails.getUsername() == null) {
            throw new AccessDeniedException(ErrorCode.FORBIDDEN.getMessage());
        }

        Like like = likeRepository.findByPost_PostIdAndUser_UserId(postId, userDetails.getUserId())
                .orElse(null);

        if(like != null) {
            like.setLikeYnTrue();
        } else {
            like = Like.builder()
                    .user(userDetails.getUser())
                    .post(post)
                    .likeYn(true)
                    .build();
        }

        likeRepository.save(like);

        LikeCount likeCount = likeCountRepository.findById(postId)
                .orElseGet(() -> LikeCount.init(postId, 0L));
        likeCount.increase();
        likeCountRepository.save(likeCount);

        challengeService.evaluateChallenges(userDetails.getUserId());

        Long receiverId = post.getUser().getUserId();
        Long senderId = userDetails.getUserId();

        String likeMessage = "";
        if(!receiverId.equals(senderId)) {
            notificationService.sendNotification(receiverId, senderId, likeMessage, NotificationType.LIKE, post.getPostId());
        }

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

    public Boolean likedByUserAndPost(Long userId, Long postId) {
        return likeRepository.findLikeYnByUserIdAndPostId(userId, postId)
                .orElse(false);
    }

    public Long getCount(Long postId) {
        return likeCountRepository.findById(postId)
                .map(LikeCount::getLikeCount)
                .orElse(0L);
    }
}
