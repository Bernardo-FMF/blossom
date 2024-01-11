package org.blossom.post.grpc.service;

import org.blossom.activitycontract.PostInfoRequest;
import org.blossom.post.dto.MetadataDto;
import org.blossom.post.grpc.client.ActivityContractGrpcClientFacade;
import org.blossom.post.grpc.streamobserver.MetadataStreamObserver;
import org.blossom.post.mapper.impl.MetadataDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class GrpcClientActivityService {
    @Autowired
    private ActivityContractGrpcClientFacade grpcClient;

    @Autowired
    private MetadataDtoMapper metadataDtoMapper;

    public Map<String, MetadataDto> getMetadata(Integer userId, List<String> postIds) throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(postIds.size());

        MetadataStreamObserver responseObserver = new MetadataStreamObserver(countDownLatch, metadataDtoMapper, userId, postIds);
        PostInfoRequest.Builder builder = PostInfoRequest.newBuilder();
        if (userId != null) {
            builder.setUserId(userId);
        }
        PostInfoRequest request = builder.addAllPostId(postIds).build();
        grpcClient.getNonBlockingStub().getPostMetadata(request, responseObserver);

        boolean await = countDownLatch.await(2, TimeUnit.MINUTES);

        return responseObserver.getMappedMetadata();
    }
}
