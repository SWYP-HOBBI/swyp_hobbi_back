package swyp.hobbi.swyphobbiback.mypage.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import swyp.hobbi.swyphobbiback.common.error.ErrorCode;
import swyp.hobbi.swyphobbiback.common.exception.CustomException;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileImageService {

    private static final String BUCKET_NAME = "hobbi-img";
    private static final String PREFIX_IMAGE_URL = "https://kr.object.ncloudstorage.com/";

    private final AmazonS3Client s3Client;

    public String uploadProfileImage(MultipartFile file, String userEmail) {
        String fileName = "profile/" + userEmail + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        try {
            metadata.setContentLength(file.getSize());
            //이미지 업로드
            s3Client.putObject(
                    new PutObjectRequest(BUCKET_NAME, fileName, file.getInputStream(), metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead)
            );
        } catch (IOException e) {
            log.error("프로필 이미지 업로드 실패 (userEmail: {}, fileName: {}): {}", userEmail, fileName, e.getMessage());
            throw new CustomException(ErrorCode.FAILED_TO_UPLOAD_FILE);
        }

        return PREFIX_IMAGE_URL + BUCKET_NAME + "/" + fileName;
    }

    //미사용 중, 향후 탈퇴/초기화 시 사용 가능
    public void deleteProfileImage(String imageUrl) {
        String key = imageUrl.replace(PREFIX_IMAGE_URL + BUCKET_NAME + "/", "");
        s3Client.deleteObject(new DeleteObjectRequest(BUCKET_NAME, key));
    }
}

