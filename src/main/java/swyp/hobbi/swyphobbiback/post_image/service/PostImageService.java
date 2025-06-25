package swyp.hobbi.swyphobbiback.post_image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import swyp.hobbi.swyphobbiback.post.domain.Post;
import swyp.hobbi.swyphobbiback.post_image.domain.PostImage;
import swyp.hobbi.swyphobbiback.post_image.repository.PostImageRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostImageService {
    private static final String BUCKET_NAME = "hobbi-dev";
    private static final String PREFIX_FILE_NAME = "post";
    private static final String FILE_FORMAT = "%s%s%s%s";
    private static final String FILE_SEPARATOR = "-";
    private static final String DIRECTORY_SEPARATOR = "/";
    private static final String PREFIX_IMAGE_URL = "https://hobbi-dev.s3.ap-northeast-2.amazonaws.com/";

    private final AmazonS3 amazonS3;

    private final PostImageRepository postImageRepository;

    public PostImage savePostImage(MultipartFile file, Post post, String imageUrl) {
        PostImage image = PostImage.builder()
                .post(post)
                .imageUrl(imageUrl)
                .imageFileName(file.getOriginalFilename())
                .build();

        return postImageRepository.save(image);
    }

    public void deletePostImage(String imageUrl) {
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(BUCKET_NAME, imageUrl);
        amazonS3.deleteObject(deleteObjectRequest);
    }

    public String fileNameFormatter(MultipartFile file) {
        return FILE_FORMAT.formatted(
                PREFIX_FILE_NAME,
                FILE_SEPARATOR,
                UUID.randomUUID().toString(),
                file.getOriginalFilename()
        );
    }

    public String generateObjectStorageUrl(String fileName) {
        return FILE_FORMAT.formatted(
                PREFIX_IMAGE_URL,
                BUCKET_NAME,
                DIRECTORY_SEPARATOR,
                fileName
        );
    }

    public String getSuffixImageUrl(String imageUrl) {
        String prefixAndBucketName = PREFIX_IMAGE_URL + BUCKET_NAME + DIRECTORY_SEPARATOR;
        return imageUrl.replace(prefixAndBucketName, "");
    }
}
