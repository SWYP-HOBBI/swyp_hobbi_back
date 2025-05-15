package swyp.hobbi.swyphobbiback.mypage.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import swyp.hobbi.swyphobbiback.common.error.ErrorCode;
import swyp.hobbi.swyphobbiback.common.exception.CustomException;
import swyp.hobbi.swyphobbiback.hobbytag.domain.HobbyTag;
import swyp.hobbi.swyphobbiback.hobbytag.repository.HobbyTagRepository;
import swyp.hobbi.swyphobbiback.mypage.dto.*;
import swyp.hobbi.swyphobbiback.post.domain.Post;
import swyp.hobbi.swyphobbiback.post.repository.PostRepository;
import swyp.hobbi.swyphobbiback.user.domain.User;
import swyp.hobbi.swyphobbiback.user.repository.UserRepository;
import swyp.hobbi.swyphobbiback.userhobbytag.domain.UserHobbyTag;


import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MypageService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;
    private final HobbyTagRepository hobbyTagRepository;
    private final ProfileImageService profileImageService;

    public MyPageResponse getMyPageInfo(Long userId) {
        User user = userRepository.findByIdWithHobbyTags(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<String> hobbyTags = user.getUserHobbyTags().stream()
                .map(tag -> tag.getHobbyTag().getHobbyTagName())
                .toList();

        return MyPageResponse.builder()
                .userId(userId)
                .username(user.getUsername())
                .nickname(user.getNickname())
                .mbti(user.getMbti())
                .userImageUrl(user.getUserImageUrl())
                .hobbyTags(hobbyTags)
                .build();
    }

    public MyModifyPageResponse getMyModifyPageInfo(Long userId) {
        User user = userRepository.findByIdWithHobbyTags(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<String> hobbyTags = user.getUserHobbyTags().stream()
                .map(tag -> tag.getHobbyTag().getHobbyTagName())
                .toList();

        return MyModifyPageResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .gender(user.getGender())
                .nickname(user.getNickname())
                .mbti(user.getMbti())
                .userImageUrl(user.getUserImageUrl())
                .birthYear(user.getBirthYear())
                .birthMonth(user.getBirthMonth())
                .birthDay(user.getBirthDay())
                .hobbyTags(hobbyTags)
                .build();
    }

    public void updateMyPageInfo(Long userId, MyPageUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.updateProfile(
                request.getUsername(),
                request.getGender(),
                request.getMbti(),
                request.getBirthYear(),
                request.getBirthMonth(),
                request.getBirthDay()
        );

        List<HobbyTag> tags = hobbyTagRepository.findAllByHobbyTagNameIn(request.getHobbyTags());

        log.debug("프론트에서 받은 태그: {}", request.getHobbyTags());
        log.debug("DB 조회된 태그 개수: {}", tags.size());

        user.updateHobbyTags(tags);

        for (UserHobbyTag tag : user.getUserHobbyTags()) {
            log.debug(" - 연결된 태그: {}", tag.getHobbyTag().getHobbyTagName());
        }

        userRepository.save(user);

    }

    public void updateNickname(Long userId, String nickname) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.setNickname(nickname);
        userRepository.save(user);
    }

    public void updatePassword(Long userId, String newPassword, String confirmPassword) {

        if (!newPassword.equals(confirmPassword)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE); //비밀번호와 비밀번호 확인 값 다를시
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String encodedPassword = passwordEncoder.encode(newPassword); // 비밀번호 암호화
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    public String uploadAndSaveProfileImage(Long userId, MultipartFile profileImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // null 또는 비어있으면 이미지 업데이트 없이 그대로 반환
        if (profileImage == null || profileImage.isEmpty()) {
            return user.getUserImageUrl(); // 기존 이미지 유지
        }

        String uploadedUrl = profileImageService.uploadProfileImage(profileImage, user.getEmail());

        try {
            user.setUserImageUrl(uploadedUrl);
            return uploadedUrl; //새로운 이미지 url 반환

        } catch (Exception e) { // DB 반영 중 예외 발생 → 업로드된 이미지 삭제
            profileImageService.deleteProfileImage(uploadedUrl);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public boolean isNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public MyPostsScrollResponse getMyPosts(Long userId, Long lastPostId, int pageSize) {
        List<Post> posts;

        Pageable pageable = PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, "postId"));

        if (lastPostId == null) {
            // 첫 페이지
            posts = postRepository.findTopByUserId(userId, pageable);
        } else {
            // 이후 페이지 (cursor 기반)
            posts = postRepository.findNextByUserId(userId, lastPostId, pageable);
        }

        List<MyPost> myPosts = posts.stream()
                .map(MyPost::from)
                .toList();

        boolean isLast = posts.size() < pageSize; // 마지막 페이지인지 체크

        return new MyPostsScrollResponse(myPosts, isLast);
    }

    public boolean checkPassword(Long userId, String currentPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return passwordEncoder.matches(currentPassword, user.getPassword());
    }

}
