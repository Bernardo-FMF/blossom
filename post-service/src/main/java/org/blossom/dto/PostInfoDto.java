package org.blossom.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PostInfoDto {
    private int userId;
    private String text;
    private MultipartFile[] mediaFiles;
}
