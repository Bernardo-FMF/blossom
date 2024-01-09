package org.blossom.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailUpdateDto {
    String newEmail;
    String oldEmail;
    String password;
}