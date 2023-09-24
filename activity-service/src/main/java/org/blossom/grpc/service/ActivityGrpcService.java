package org.blossom.grpc.service;

import io.grpc.stub.StreamObserver;
import org.blossom.activitycontract.ActivityContractGrpc;
import org.blossom.activitycontract.PostInfoRequest;
import org.blossom.activitycontract.PostInfoResponse;
import org.blossom.dto.MetadataDto;
import org.blossom.service.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivityGrpcService extends ActivityContractGrpc.ActivityContractImplBase {
    @Autowired
    private MetadataService metadataService;

    @Override
    public void getPostMetadata(PostInfoRequest request, StreamObserver<PostInfoResponse> responseObserver) {
        Integer userId = request.getUserId();

        for (String postId: request.getPostIdList()) {
            MetadataDto metadata = metadataService.getPostMetadata(postId, userId);
            PostInfoResponse.Builder metadataBuilder = PostInfoResponse.newBuilder();
            metadataBuilder.setUserCommented(metadata.getCommentMetadata().isUserCommented())
                    .setUserSavedPost(metadata.getInteractionMetadata().isUserSaved())
                    .setUserLikedPost(metadata.getInteractionMetadata().isUserLiked())
                    .setTotalLikes(metadata.getInteractionMetadata().getLikeCount())
                    .setTotalComments(metadata.getCommentMetadata().getCommentCount());
            responseObserver.onNext(metadataBuilder.build());
        }
        responseObserver.onCompleted();
    }
}
