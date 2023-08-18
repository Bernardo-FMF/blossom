package org.blossom.dto;

import lombok.Getter;
import lombok.Setter;
import org.blossom.enums.Visibility;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PostInfoDto {
    private int userId;
    private String text;
    private Visibility visibility;
    private MultipartFile[] mediaFiles;
}
