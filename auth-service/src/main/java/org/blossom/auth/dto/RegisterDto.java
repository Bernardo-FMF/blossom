package org.blossom.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDto {
    private String email;
    private String fullName;
    private String username;
    private String password;

    @Override
    public String toString() {
        return "RegisterDto{" +
                "email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}