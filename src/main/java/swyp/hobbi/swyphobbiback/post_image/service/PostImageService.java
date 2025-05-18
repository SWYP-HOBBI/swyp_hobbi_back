package swyp.hobbi.swyphobbiback.post_image.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import swyp.hobbi.swyphobbiback.post.domain.Post;
import swyp.hobbi.swyphobbiback.post_image.domain.PostImage;
import swyp.hobbi.swyphobbiback.post_image.repository.PostImageRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostImageService {
    private static final String BUCKET_NAME = "hobbi-img";
    private static final String PREFIX_FILE_NAME = "post";
    private static final String FILE_FORMAT = "%s%s%s%s";
    private static final String FILE_SEPARATOR = "-";
    private static final String DIRECTORY_SEPARATOR = "/";
    private static final String PREFIX_IMAGE_URL = "https://kr.object.ncloudstorage.com/";

    private final AmazonS3Client objectStorageClient;

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
        objectStorageClient.deleteObject(deleteObjectRequest);
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

    public List<Long> getImageFileSizes(List<String> imageUrls) {
        List<Long> imageFileSizes = new ArrayList<>();
        for (String imageUrl : imageUrls) {
            S3Object s3Object = objectStorageClient.getObject(BUCKET_NAME, imageUrl);
            imageFileSizes.add(s3Object.getObjectMetadata().getContentLength());
        }

        return imageFileSizes;
    }
}
