package org.blossom.projection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
public class CommentCountProjection {
    private int commentCount;
    private boolean hasUserCommented;
}
