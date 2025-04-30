package swyp.hobbi.swyphobbiback.post_image.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import swyp.hobbi.swyphobbiback.common.exception.FileUploadFailedException;
import swyp.hobbi.swyphobbiback.post.domain.Post;
import swyp.hobbi.swyphobbiback.post_image.domain.PostImage;
import swyp.hobbi.swyphobbiback.post_image.repository.PostImageRepository;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostImageService {
    private static final String BUCKET_NAME = "hobbi-img";
    private static final String PREFIX_FILE_NAME = "post";
    private static final String FILE_FORMAT = "%s%s%s%s";
    private static final String FILE_SEPARATOR = "-";

    private final AmazonS3Client objectStorageClient;

    private final PostImageRepository postImageRepository;

    public String uploadImage(MultipartFile file) {
        String fileName = fileNameFormatter(file);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, fileName, file.getInputStream(), metadata);
            objectStorageClient.putObject(request);
        } catch (IOException e) {
            throw new FileUploadFailedException();
        }

        return objectStorageClient.getUrl(BUCKET_NAME, fileName).toString();
    }

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

    private static String fileNameFormatter(MultipartFile file) {
        return FILE_FORMAT.formatted(
                PREFIX_FILE_NAME,
                FILE_SEPARATOR,
                UUID.randomUUID().toString(),
                file.getOriginalFilename()
        );
    }
}
