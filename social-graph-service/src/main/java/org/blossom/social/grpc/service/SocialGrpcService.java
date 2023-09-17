package org.blossom.social.grpc.service;

import io.grpc.stub.StreamObserver;
import org.blossom.social.repository.SocialRepository;
import org.blossom.socialcontract.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SocialGrpcService extends SocialContractGrpc.SocialContractImplBase {
    @Autowired
    private SocialRepository socialRepository;

    @Override
    public void getUserFollowers(UserRequest request, StreamObserver<FollowersResponse> responseObserver) {
        int userId = request.getId();

        List<Integer> followers = socialRepository.findFollowersUnpaged(userId);

        responseObserver.onNext(FollowersResponse.newBuilder().addAllUserId(followers).build());
        responseObserver.onCompleted();
    }

    @Override
    public void getMostFollowed(MostFollowedRequest request, StreamObserver<MostFollowedResponse> responseObserver) {

    }
}
