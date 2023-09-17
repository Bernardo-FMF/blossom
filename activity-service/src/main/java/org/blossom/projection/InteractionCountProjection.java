package org.blossom.projection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
public class InteractionCountProjection {
    private long likeCount;
    private boolean userLiked;
    private boolean userSaved;

    public InteractionCountProjection(long likeCount) {
        this.likeCount = likeCount;
        this.userLiked = false;
        this.userSaved = false;
    }
}
