package org.blossom.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MfaVerificationDto {
    private String email;
    private String code;
}
