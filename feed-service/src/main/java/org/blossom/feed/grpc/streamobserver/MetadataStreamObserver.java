package org.blossom.feed.grpc.streamobserver;

import io.grpc.stub.StreamObserver;
import org.blossom.activitycontract.PostInfoResponse;
import org.blossom.feed.dto.MetadataDto;
import org.blossom.feed.mapper.MetadataDtoMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class MetadataStreamObserver implements StreamObserver<PostInfoResponse> {
    private final CountDownLatch countDownLatch;
    private final MetadataDtoMapper metadataDtoMapper;
    private final Integer userId;
    private final Map<String, MetadataDto> mappedMetadata;

    public MetadataStreamObserver(CountDownLatch countDownLatch, MetadataDtoMapper metadataDtoMapper, Integer userId, List<String> postIds) {
        this.countDownLatch = countDownLatch;
        this.metadataDtoMapper = metadataDtoMapper;
        this.userId = userId;
        this.mappedMetadata = new HashMap<>();
        for (String postId: postIds) {
            mappedMetadata.put(postId, null);
        }
    }

    @Override
    public void onNext(PostInfoResponse postInfoResponse) {
        MetadataDto metadataDto = metadataDtoMapper.mapToMetadataDto(userId, postInfoResponse);
        mappedMetadata.put(metadataDto.getPostId(), metadataDto);
        countDownLatch.countDown();
    }

    @Override
    public void onError(Throwable throwable) {
        countDownLatch.countDown();
    }

    @Override
    public void onCompleted() {

    }

    public Map<String, MetadataDto> getMappedMetadata() {
        return mappedMetadata;
    }
}
