package org.blossom.social.service;

import org.blossom.social.dto.GraphUserDto;
import org.blossom.social.dto.RecommendationsDto;
import org.blossom.social.dto.SearchParametersDto;
import org.blossom.social.dto.SocialRelationDto;
import org.blossom.social.entity.GraphUser;
import org.blossom.social.exception.FollowNotValidException;
import org.blossom.social.exception.UserNotFoundException;
import org.blossom.social.kafka.outbound.KafkaMessageService;
import org.blossom.social.kafka.outbound.model.SocialFollow;
import org.blossom.social.mapper.LocalUserMapper;
import org.blossom.social.repository.SocialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.stream.Collectors;

@Service
public class SocialService {
    @Autowired
    private SocialRepository socialRepository;

    @Autowired
    private KafkaMessageService messageService;

    @Autowired
    private LocalUserMapper localUserMapper;

    public String createSocialRelation(SocialRelationDto socialRelationDto, int userId) throws FollowNotValidException {
        if (socialRelationDto.getInitiatingUser() != userId) {
            throw new FollowNotValidException("Could not perform operation on this user");
        }

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

        SocialFollow socialFollow = SocialFollow.builder()
                .initiatingUser(socialRelationDto.getInitiatingUser())
                .receivingUser(socialRelationDto.getReceivingUser())
                .isMutualFollow(socialRepository.existsRelationshipBetweenUsers(socialRelationDto.getReceivingUser(), socialRelationDto.getInitiatingUser()))
                .createdAt(new Date())
                .build();

        messageService.publishCreation(socialFollow);

        return "Relation was created successfully";
    }

    public String deleteSocialRelation(SocialRelationDto socialRelationDto, int userId) throws FollowNotValidException, UserNotFoundException {
        if (socialRelationDto.getInitiatingUser() != userId) {
            throw new FollowNotValidException("Could not perform operation on this user");
        }

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

        SocialFollow socialFollow = SocialFollow.builder()
                .initiatingUser(socialRelationDto.getInitiatingUser())
                .receivingUser(socialRelationDto.getReceivingUser())
                .isMutualFollow(false)
                .createdAt(new Date())
                .build();
        messageService.publishCreation(socialFollow);

        return "Relation was deleted successfully";
    }

    public RecommendationsDto getFollowRecommendations(SearchParametersDto searchParameters, int userId) throws UserNotFoundException {
        if (!socialRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }

        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit()) : null;

        Page<GraphUser> recommendations = socialRepository.findRecommendations(userId, page);

        return RecommendationsDto.builder()
                .userId(userId)
                .recommendations(recommendations.stream().map(user -> localUserMapper.mapToLocalUser(user)).collect(Collectors.toList()))
                .currentPage(recommendations.getNumber())
                .totalPages(recommendations.getTotalPages())
                .totalElements(recommendations.getTotalElements())
                .eof(!recommendations.hasNext())
                .build();
    }

    public GraphUserDto getUserFollowers(SearchParametersDto searchParameters, int userId) throws UserNotFoundException {
        if (!socialRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }

        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit()) : null;

        Page<GraphUser> followers = socialRepository.findFollowers(userId, page);

        return GraphUserDto.builder()
                .userId(userId)
                .otherUsers(followers.stream().map(graphUser -> localUserMapper.mapToLocalUser(graphUser)).collect(Collectors.toList()))
                .totalPages(followers.getTotalPages())
                .currentPage(searchParameters.getPage())
                .totalElements(followers.getTotalElements())
                .eof(!followers.hasNext())
                .build();
    }

    public GraphUserDto getUserFollowings(SearchParametersDto searchParameters, int userId) throws UserNotFoundException {
        if (!socialRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }

        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit()) : null;

        Page<GraphUser> followers = socialRepository.findFollowing(userId, page);

        return GraphUserDto.builder()
                .userId(userId)
                .otherUsers(followers.stream().map(graphUser -> localUserMapper.mapToLocalUser(graphUser)).collect(Collectors.toList()))
                .totalPages(followers.getTotalPages())
                .currentPage(searchParameters.getPage())
                .totalElements(followers.getTotalElements())
                .eof(!followers.hasNext())
                .build();
    }
}
