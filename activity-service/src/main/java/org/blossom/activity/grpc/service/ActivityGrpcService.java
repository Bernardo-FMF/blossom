package org.blossom.activity.grpc.service;

import io.grpc.stub.StreamObserver;
import org.blossom.activity.dto.MetadataDto;
import org.blossom.activity.exception.PostNotFoundException;
import org.blossom.activity.mapper.impl.MetadataGrpcMapper;
import org.blossom.activity.service.MetadataService;
import org.blossom.activitycontract.ActivityContractGrpc;
import org.blossom.activitycontract.PostInfoRequest;
import org.blossom.activitycontract.PostInfoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivityGrpcService extends ActivityContractGrpc.ActivityContractImplBase {
    @Autowired
    private MetadataService metadataService;

    @Autowired
    private MetadataGrpcMapper metadataGrpcMapper;

    @Override
    public void getPostMetadata(PostInfoRequest request, StreamObserver<PostInfoResponse> responseObserver) {
        Integer userId = request.getUserId();

        for (String postId: request.getPostIdList()) {
            try {
                MetadataDto metadata = metadataService.getPostMetadata(postId, userId);
                responseObserver.onNext(metadataGrpcMapper.toDto(metadata));
            } catch (PostNotFoundException e) {
                responseObserver.onError(e);
            }
        }
        responseObserver.onCompleted();
    }
}
