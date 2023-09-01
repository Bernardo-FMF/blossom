package org.blossom.social.grpc.service;

import io.grpc.stub.StreamObserver;
import org.blossom.social.entity.GraphUser;
import org.blossom.social.repository.SocialRepository;
import org.blossom.socialcontract.Followers;
import org.blossom.socialcontract.SocialContractGrpc;
import org.blossom.socialcontract.UserId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SocialGrpcService extends SocialContractGrpc.SocialContractImplBase {
    @Autowired
    private SocialRepository socialRepository;

    @Override
    public void getUserFollowers(UserId request, StreamObserver<Followers> responseObserver) {
        int userId = request.getId();

        List<GraphUser> followers = socialRepository.findFollowersUnpaged(userId);

        responseObserver.onNext(Followers.newBuilder().addAllUserId(followers.stream().map(GraphUser::getUserId).toList()).build());
        responseObserver.onCompleted();
    }
}
