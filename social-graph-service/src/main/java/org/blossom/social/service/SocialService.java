package org.blossom.social.service;

import org.blossom.social.dto.*;
import org.blossom.social.entity.GraphUser;
import org.blossom.social.exception.FollowNotValidException;
import org.blossom.social.exception.UserNotFoundException;
import org.blossom.social.factory.impl.SocialFollowFactory;
import org.blossom.social.kafka.outbound.KafkaMessageService;
import org.blossom.social.kafka.outbound.model.SocialFollow;
import org.blossom.social.mapper.impl.*;
import org.blossom.social.repository.SocialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SocialService {
    @Autowired
    private SocialRepository socialRepository;

    @Autowired
    private KafkaMessageService messageService;

    @Autowired
    private UserDtoMapper userDtoMapper;

    @Autowired
    private GenericDtoMapper genericDtoMapper;

    @Autowired
    private RecommendationsDtoMapper recommendationsDtoMapper;

    @Autowired
    private GraphUserDtoMapper graphUserDtoMapper;

    @Autowired
    private FollowCountDtoMapper followCountDtoMapper;

    @Autowired
    private SocialFollowFactory socialFollowFactory;

    public GenericResponseDto createSocialRelation(SocialRelationDto socialRelationDto, int userId) throws FollowNotValidException {
        if (userId == socialRelationDto.getReceivingUser()) {
            throw new FollowNotValidException("Follow not valid. A user cannot follow itself");
        }

        if (!socialRepository.existsById(userId) || !socialRepository.existsById(socialRelationDto.getReceivingUser())) {
            throw new FollowNotValidException("Users not found");
        }

        if (socialRepository.existsRelationshipBetweenUsers(userId, socialRelationDto.getReceivingUser())) {
            throw new FollowNotValidException("User is already following the requested user");
        }

        socialRepository.createFollowerRelationship(userId, socialRelationDto.getReceivingUser());

        SocialFollow socialFollow = socialFollowFactory.buildEntity(socialRelationDto, userId, socialRepository.existsRelationshipBetweenUsers(socialRelationDto.getReceivingUser(), userId));

        messageService.publishCreation(socialFollow);

        return genericDtoMapper.toDto("Follow was created successfully", userId, null);
    }

    public GenericResponseDto deleteSocialRelation(SocialRelationDto socialRelationDto, int userId) throws FollowNotValidException, UserNotFoundException {
        if (userId == socialRelationDto.getReceivingUser()) {
            throw new FollowNotValidException("Follow not valid. A user cannot follow itself");
        }

        if (!socialRepository.existsById(userId) || !socialRepository.existsById(socialRelationDto.getReceivingUser())) {
            throw new UserNotFoundException("Users not found");
        }

        if (!socialRepository.existsRelationshipBetweenUsers(userId, socialRelationDto.getReceivingUser())) {
            throw new FollowNotValidException("User is not following the requested user");
        }

        socialRepository.deleteFollowerRelationship(userId, socialRelationDto.getReceivingUser());

        SocialFollow socialFollow = socialFollowFactory.buildEntity(socialRelationDto, userId, false);

        messageService.publishCreation(socialFollow);

        return genericDtoMapper.toDto("Follow was deleted successfully", userId, null);
    }

    public RecommendationsDto getFollowRecommendations(SearchParametersDto searchParameters, int userId) throws UserNotFoundException {
        if (!socialRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }

        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit()) : Pageable.unpaged();

        Page<GraphUser> recommendations = socialRepository.findRecommendations(userId, page);

        PaginationInfoDto paginationInfo = recommendationsDtoMapper.createPaginationInfo(searchParameters.getPage(), recommendations.getTotalPages(), recommendations.getTotalElements(), !recommendations.hasNext());
        return recommendationsDtoMapper.toPaginatedDto(userId, recommendations.getContent(), paginationInfo);
    }

    public GraphUserDto getUserFollowers(SearchParametersDto searchParameters, int userId) throws UserNotFoundException {
        if (!socialRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }

        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit()) : Pageable.unpaged();

        Page<GraphUser> followers = socialRepository.findFollowers(userId, page);

        PaginationInfoDto paginationInfo = recommendationsDtoMapper.createPaginationInfo(searchParameters.getPage(), followers.getTotalPages(), followers.getTotalElements(), !followers.hasNext());
        return graphUserDtoMapper.toPaginatedDto(userId, followers.getContent(), paginationInfo);
    }

    public GraphUserDto getUserFollowings(SearchParametersDto searchParameters, int userId) throws UserNotFoundException {
        if (!socialRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }

        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit()) : Pageable.unpaged();

        Page<GraphUser> followers = socialRepository.findFollowing(userId, page);

        PaginationInfoDto paginationInfo = recommendationsDtoMapper.createPaginationInfo(searchParameters.getPage(), followers.getTotalPages(), followers.getTotalElements(), !followers.hasNext());
        return graphUserDtoMapper.toPaginatedDto(userId, followers.getContent(), paginationInfo);
    }

    public FollowCountDto getFollowCount(int userId) throws UserNotFoundException {
        if (!socialRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }

        long followCount = socialRepository.findFollowCount(userId);
        long followerCount = socialRepository.findFollowerCount(userId);

        return followCountDtoMapper.toDto(userId, followCount, followerCount);
    }
}
