package org.blossom.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerificationDto {
    int userId;
    String token;
}
