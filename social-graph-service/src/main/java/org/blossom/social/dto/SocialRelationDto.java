package org.blossom.social.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocialRelationDto {
    int initiatingUser;
    int receivingUser;
}
