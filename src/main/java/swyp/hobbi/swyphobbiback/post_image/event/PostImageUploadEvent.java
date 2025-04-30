package swyp.hobbi.swyphobbiback.post_image.event;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@RequiredArgsConstructor
@Builder
public class PostImageUploadEvent {
    private final Long postImageId;
    private final MultipartFile file;
    private final String fileName;
}
