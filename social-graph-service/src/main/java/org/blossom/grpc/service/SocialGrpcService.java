package org.blossom.grpc.service;

import io.grpc.stub.StreamObserver;
import org.blossom.repository.SocialRepository;
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

        List<Integer> followers = socialRepository.findFollowers(userId);

        responseObserver.onNext(Followers.newBuilder().addAllUserId(followers).build());
        responseObserver.onCompleted();
    }
}
