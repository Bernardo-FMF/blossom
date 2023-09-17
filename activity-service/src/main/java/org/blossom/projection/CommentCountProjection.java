package org.blossom.projection;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class CommentCountProjection {
    private long commentCount;
    private boolean userCommented;

    public CommentCountProjection(long commentCount) {
        this.commentCount = commentCount;
        this.userCommented = false;
    }

    public CommentCountProjection(long commentCount, boolean userCommented) {
        this.commentCount = commentCount;
        this.userCommented = userCommented;
    }
}
