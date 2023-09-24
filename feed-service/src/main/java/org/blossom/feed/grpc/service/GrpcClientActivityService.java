package org.blossom.feed.grpc.service;

import org.blossom.activitycontract.PostInfoRequest;
import org.blossom.feed.dto.MetadataDto;
import org.blossom.feed.grpc.client.ActivityContractGrpcClientFacade;
import org.blossom.feed.grpc.streamobserver.MetadataStreamObserver;
import org.blossom.feed.mapper.MetadataDtoMapper;
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
        grpcClient.getNonBlockingStub().getPostMetadata(PostInfoRequest.newBuilder().setUserId(userId).addAllPostId(postIds).build(), responseObserver);

        boolean await = countDownLatch.await(2, TimeUnit.MINUTES);

        return responseObserver.getMappedMetadata();
    }
}
