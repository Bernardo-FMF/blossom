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
    private int likeCount;
    private int saveCount;
    private boolean hasUserLiked;
    private boolean hasUserSaved;
}
