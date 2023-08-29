package org.blossom.grpc.service;

import io.grpc.stub.StreamObserver;
import org.blossom.activitycontract.ActivityContractGrpc;
import org.blossom.activitycontract.PostInfoRequest;
import org.blossom.activitycontract.PostInfoResponse;
import org.blossom.projection.CommentCountProjection;
import org.blossom.projection.InteractionCountProjection;
import org.blossom.repository.CommentRepository;
import org.blossom.repository.InteractionRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class ActivityGrpcService extends ActivityContractGrpc.ActivityContractImplBase {
    @Autowired
    private InteractionRepository interactionRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public void getPostMetadata(PostInfoRequest request, StreamObserver<PostInfoResponse> responseObserver) {
        int userId = request.getUserId();

        for (String postId: request.getPostIdList()) {
            InteractionCountProjection interactionCount = interactionRepository.getInteractionCount(postId, userId);
            CommentCountProjection commentCount = commentRepository.getCommentCount(postId);
            responseObserver.onNext(PostInfoResponse.newBuilder()
                    .setUserCommented(commentCount.isHasUserCommented())
                    .setTotalComments(commentCount.getCommentCount())
                    .setTotalLikes(interactionCount.getLikeCount())
                    .setTotalSaves(interactionCount.getSaveCount())
                    .setUserLikedPost(interactionCount.isHasUserLiked())
                    .setUserSavedPost(interactionCount.isHasUserSaved())
                    .build());
        }
        responseObserver.onCompleted();
    }
}
