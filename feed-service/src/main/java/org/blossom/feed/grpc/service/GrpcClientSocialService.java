package org.blossom.feed.grpc.service;

import org.blossom.feed.grpc.client.SocialContractGrpcClientFacade;
import org.blossom.socialcontract.FollowersResponse;
import org.blossom.socialcontract.MostFollowedRequest;
import org.blossom.socialcontract.MostFollowedResponse;
import org.blossom.socialcontract.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrpcClientSocialService {
    @Autowired
    private SocialContractGrpcClientFacade grpcClient;

    public List<Integer> getUserFollowers(int userId) {
        FollowersResponse userFollowers = grpcClient.buildBlockingStub().getUserFollowers(UserRequest.newBuilder().setId(userId).build());
        return userFollowers.getUserIdList();
    }

    public List<Integer> getMostFollowed() {
        MostFollowedResponse mostFollowed = grpcClient.buildBlockingStub().getMostFollowed(MostFollowedRequest.newBuilder().build());
        return mostFollowed.getUsersList();
    }
}
