package org.blossom.service;

import org.blossom.cache.LocalUserCacheService;
import org.blossom.dto.GraphUserDto;
import org.blossom.dto.RecommendationsDto;
import org.blossom.dto.SearchParametersDto;
import org.blossom.dto.SocialRelationDto;
import org.blossom.entity.GraphUser;
import org.blossom.exception.FollowNotValidException;
import org.blossom.exception.UserNotFoundException;
import org.blossom.kafka.inbound.model.LocalUser;
import org.blossom.repository.SocialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SocialService {
    @Autowired
    private SocialRepository socialRepository;

    @Autowired
    private LocalUserCacheService localUserCache;

    public String createSocialRelation(SocialRelationDto socialRelationDto) throws FollowNotValidException {
        if (socialRelationDto.getInitiatingUser() == socialRelationDto.getReceivingUser()) {
            throw new FollowNotValidException("Follow not valid. A user cannot follow itself");
        }

        if (!socialRepository.existsById(socialRelationDto.getInitiatingUser()) || !socialRepository.existsById(socialRelationDto.getReceivingUser())) {
            throw new FollowNotValidException("Users not found");
        }

        if (socialRepository.existsRelationshipBetweenUsers(socialRelationDto.getInitiatingUser(), socialRelationDto.getReceivingUser())) {
            throw new FollowNotValidException("User is already following the requested user");
        }

        socialRepository.createFollowerRelationship(socialRelationDto.getInitiatingUser(), socialRelationDto.getReceivingUser());

        return "Relation was created successfully";
    }

    public String deleteSocialRelation(SocialRelationDto socialRelationDto) throws FollowNotValidException, UserNotFoundException {
        if (socialRelationDto.getInitiatingUser() == socialRelationDto.getReceivingUser()) {
            throw new FollowNotValidException("Follow not valid. A user cannot follow itself");
        }

        if (!socialRepository.existsById(socialRelationDto.getInitiatingUser()) || !socialRepository.existsById(socialRelationDto.getReceivingUser())) {
            throw new UserNotFoundException("Users not found");
        }

        if (!socialRepository.existsRelationshipBetweenUsers(socialRelationDto.getInitiatingUser(), socialRelationDto.getReceivingUser())) {
            throw new FollowNotValidException("User is not following the requested user");
        }

        socialRepository.deleteFollowerRelationship(socialRelationDto.getInitiatingUser(), socialRelationDto.getReceivingUser());

        return "Relation was deleted successfully";
    }

    public GraphUserDto getUserSocialGraph(int userId) throws UserNotFoundException {
        Optional<GraphUser> optionalUser = socialRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("Users not found");
        }

        GraphUser user = optionalUser.get();
        List<Integer> followers = socialRepository.findFollowers(userId);

        Set<String> combinedSet = new HashSet<>();
        combinedSet.add(String.valueOf(userId));
        combinedSet.addAll(followers.stream().map(String::valueOf).toList());
        combinedSet.addAll(user.getFollowing().stream().map(graphUser -> String.valueOf(graphUser.getUserId())).toList());

        Map<Integer, LocalUser> allUsers = localUserCache.getMultiFromCache(new ArrayList<>(combinedSet)).stream()
                .collect(Collectors.toMap(LocalUser::getId, localUser -> localUser));

        return GraphUserDto.builder()
                .user(allUsers.get(userId))
                .follows(user.getFollowing().stream().map(graphUser -> allUsers.get(graphUser.getUserId())).collect(Collectors.toSet()))
                .followers(followers.stream().map(allUsers::get).collect(Collectors.toSet()))
                .build();
    }

    public RecommendationsDto getFollowRecommendations(SearchParametersDto searchParameters, int userId) throws UserNotFoundException {
        Optional<GraphUser> optionalUser = socialRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("Users not found");
        }

        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit()) : null;

        Page<Integer> recommendations = socialRepository.findRecommendations(userId, page);
        Map<Integer, LocalUser> allUsers = localUserCache.getMultiFromCache(recommendations.get().map(String::valueOf).toList()).stream()
                .collect(Collectors.toMap(LocalUser::getId, localUser -> localUser));

        return RecommendationsDto.builder()
                .recommendations(recommendations.stream().map(allUsers::get).collect(Collectors.toList()))
                .currentPage(recommendations.getNumber())
                .totalPages(recommendations.getTotalPages())
                .totalElements(recommendations.getTotalElements())
                .eof(!recommendations.hasNext())
                .build();
    }
}
