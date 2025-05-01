package swyp.hobbi.swyphobbiback.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import swyp.hobbi.swyphobbiback.common.exception.FileUploadFailedException;
import swyp.hobbi.swyphobbiback.common.exception.PostNotFoundException;
import swyp.hobbi.swyphobbiback.common.security.CustomUserDetails;
import swyp.hobbi.swyphobbiback.hobbytag.domain.HobbyTag;
import swyp.hobbi.swyphobbiback.hobbytag.repository.HobbyTagRepository;
import swyp.hobbi.swyphobbiback.post.domain.Post;
import swyp.hobbi.swyphobbiback.post.dto.PostCreateRequest;
import swyp.hobbi.swyphobbiback.post.dto.PostResponse;
import swyp.hobbi.swyphobbiback.post.repository.PostRepository;
import swyp.hobbi.swyphobbiback.post_hobbytag.domain.PostHobbyTag;
import swyp.hobbi.swyphobbiback.post_image.domain.PostImage;
import swyp.hobbi.swyphobbiback.post_image.event.PostImageUploadEvent;
import swyp.hobbi.swyphobbiback.post_image.service.PostImageService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostImageService postImageService;
    private final HobbyTagRepository hobbyTagRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public PostResponse create(CustomUserDetails userDetails, PostCreateRequest request, List<MultipartFile> imageFiles) {
        Post post = Post.builder()
                .user(userDetails.getUser())
                .postTitle(request.getTitle())
                .postContent(request.getContent())
                .build();

        postRepository.save(post);

        List<String> uploadedImageUrls = new ArrayList<>();
        List<PostImage> postImages = post.getPostImages();

        try {
            if(!imageFiles.isEmpty()) {
                for(MultipartFile imageFile : imageFiles) {
                    String generatedUniqueFileName = postImageService.fileNameFormatter(imageFile);
                    String imageUrl = postImageService.getObjectStorageUrl(generatedUniqueFileName);
                    PostImage savedPostImage = postImageService.savePostImage(imageFile, post, imageUrl);
                    postImages.add(savedPostImage);

                    eventPublisher.publishEvent(PostImageUploadEvent.builder()
                            .postImageId(savedPostImage.getImageId())
                            .file(imageFile)
                            .fileName(generatedUniqueFileName)
                            .build()
                    );
                    uploadedImageUrls.add(imageUrl);
                }
            }
        } catch (Exception e) {
            for(String uploadedImageUrl : uploadedImageUrls) {
                postImageService.deletePostImage(uploadedImageUrl);
            }

            throw new FileUploadFailedException();
        }

        List<PostHobbyTag> postHobbyTags = post.getPostHobbyTags();
        if(!request.getHobbyTagNames().isEmpty()) {
            List<HobbyTag> hobbyTags = hobbyTagRepository.findAllByHobbyTagNameIn(request.getHobbyTagNames());
            postHobbyTags = hobbyTags.stream()
                    .map(hobbyTag -> PostHobbyTag.builder()
                            .post(post)
                            .hobbyTag(hobbyTag)
                            .build()
                    )
                    .toList();
        }

        post.getPostHobbyTags().addAll(postHobbyTags);

        return PostResponse.from(post);
    }

    public PostResponse findPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        return PostResponse.from(post);
    }
}
