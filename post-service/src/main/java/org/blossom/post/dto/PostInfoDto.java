package org.blossom.post.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PostInfoDto {
    private String text;
    private MultipartFile[] mediaFiles;
    private String[] hashtags;
    private String[] mediaUrls;
    private Integer userId;
}
