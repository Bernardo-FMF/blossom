package org.blossom.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeDto {
    int userId;
    String newPassword;
    String token;
}
