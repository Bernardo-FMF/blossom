package org.blossom.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PasswordChangeDto {
    private String oldPassword;
    private String newPassword;
    private String newPasswordConfirmation;
}
