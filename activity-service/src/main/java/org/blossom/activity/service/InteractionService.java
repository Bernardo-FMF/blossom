package org.blossom.activity.service;

import org.blossom.activity.cache.LocalPostCacheService;
import org.blossom.activity.dto.*;
import org.blossom.activity.entity.Interaction;
import org.blossom.activity.entity.LocalUser;
import org.blossom.activity.enums.InteractionType;
import org.blossom.activity.exception.*;
import org.blossom.activity.dto.PostDto;
import org.blossom.activity.mapper.impl.InteractionDtoMapper;
import org.blossom.activity.factory.impl.InteractionFactory;
import org.blossom.activity.mapper.impl.GenericDtoMapper;
import org.blossom.activity.mapper.impl.UserInteractionsDtoMapper;
import org.blossom.activity.repository.InteractionRepository;
import org.blossom.activity.repository.LocalUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InteractionService {
    @Autowired
    private InteractionRepository interactionRepository;

    @Autowired
    private LocalPostCacheService localPostCache;

    @Autowired
    private InteractionFactory interactionFactory;

    @Autowired
    private InteractionDtoMapper interactionDtoMapper;

    @Autowired
    private LocalUserRepository localUserRepository;

    @Autowired
    private GenericDtoMapper genericDtoMapper;

    @Autowired
    private UserInteractionsDtoMapper userInteractionsDtoMapper;

    public GenericResponseDto createLike(InteractionInfoDto interactionInfoDto, int userId) throws OperationNotAllowedException, UserNotFoundException, PostNotFoundException, InteractionAlreadyExistsException {
        if (interactionInfoDto.getUserId() != userId) {
            throw new OperationNotAllowedException("Logged in user cannot perform this operation");
        }

        Optional<LocalUser> optionalLocalUser = localUserRepository.findById(userId);
        if (optionalLocalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        if (!localPostCache.findEntry(interactionInfoDto.getPostId())) {
            throw new PostNotFoundException("Post not found");
        }

        if (interactionRepository.existsByUserIdAndPostIdAndInteractionType(userId, interactionInfoDto.getPostId(), InteractionType.LIKE)) {
            throw new InteractionAlreadyExistsException("Like already exists");
        }

        Interaction interaction = interactionFactory.buildEntity(interactionInfoDto, optionalLocalUser.get(), InteractionType.LIKE);

        Interaction newInteraction = interactionRepository.save(interaction);

        return genericDtoMapper.toDto("Like was created successfully", newInteraction.getId(), null);
    }

    public GenericResponseDto createSave(InteractionInfoDto interactionInfoDto, int userId) throws OperationNotAllowedException, UserNotFoundException, PostNotFoundException, InteractionAlreadyExistsException {
        if (interactionInfoDto.getUserId() != userId) {
            throw new OperationNotAllowedException("Logged in user cannot perform this operation");
        }

        Optional<LocalUser> optionalLocalUser = localUserRepository.findById(userId);
        if (optionalLocalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        if (!localPostCache.findEntry(interactionInfoDto.getPostId())) {
            throw new PostNotFoundException("Post not found");
        }

        if (interactionRepository.existsByUserIdAndPostIdAndInteractionType(userId, interactionInfoDto.getPostId(), InteractionType.SAVE)) {
            throw new InteractionAlreadyExistsException("Save already exists");
        }

        Interaction interaction = interactionFactory.buildEntity(interactionInfoDto, optionalLocalUser.get(), InteractionType.SAVE);

        Interaction newInteraction = interactionRepository.save(interaction);

        return genericDtoMapper.toDto("Save was created successfully", newInteraction.getId(), null);
    }

    public GenericResponseDto deleteLike(Integer interactionId, int userId) throws InteractionNotFoundException, OperationNotAllowedException, UserNotFoundException, PostNotFoundException {
        Optional<Interaction> optionalInteraction = interactionRepository.findById(interactionId);
        if (optionalInteraction.isEmpty()) {
            throw new InteractionNotFoundException("Like not found");
        }

        Interaction interaction = optionalInteraction.get();

        if (interaction.getUser().getId() != userId) {
            throw new OperationNotAllowedException("Logged in user cannot perform this operation");
        }

        if (!localPostCache.findEntry(interaction.getPostId())) {
            throw new PostNotFoundException("Post not found");
        }

        interactionRepository.deleteById(interactionId);

        return genericDtoMapper.toDto("Like was deleted successfully", interactionId, null);
    }

    public GenericResponseDto deleteSave(Integer interactionId, int userId) throws InteractionNotFoundException, OperationNotAllowedException, UserNotFoundException, PostNotFoundException {
        Optional<Interaction> optionalInteraction = interactionRepository.findById(interactionId);
        if (optionalInteraction.isEmpty()) {
            throw new InteractionNotFoundException("Save not found");
        }

        Interaction interaction = optionalInteraction.get();

        if (interaction.getUser().getId() != userId) {
            throw new OperationNotAllowedException("Logged in user cannot perform this operation");
        }

        if (!localPostCache.findEntry(interaction.getPostId())) {
            throw new PostNotFoundException("Post not found");
        }

        interactionRepository.deleteById(interactionId);

        return genericDtoMapper.toDto("Save was deleted successfully", interactionId, null);
    }

    public UserInteractionsDto getUserLikes(SearchParametersDto searchParameters, int userId) throws UserNotFoundException {
        Optional<LocalUser> optionalLocalUser = localUserRepository.findById(userId);
        if (optionalLocalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit()) : null;

        Page<Interaction> likes = interactionRepository.findByUserIdAndInteractionType(userId, InteractionType.LIKE, page);

        Map<String, PostDto> allPosts = likes.stream().map(like -> localPostCache.getFromCache(like.getPostId()))
                .collect(Collectors.toMap(PostDto::getPostId, post -> post));

        PaginationInfoDto paginationInfo = new PaginationInfoDto(likes.getTotalPages(), searchParameters.getPage(), likes.getTotalElements(), !likes.hasNext());
        return userInteractionsDtoMapper.toDto(optionalLocalUser.get(), InteractionType.LIKE, likes.getContent(), allPosts, paginationInfo);
    }

    public UserInteractionsDto getUserSaves(SearchParametersDto searchParameters, int userId) throws UserNotFoundException {
        Optional<LocalUser> optionalLocalUser = localUserRepository.findById(userId);
        if (optionalLocalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit()) : null;

        Page<Interaction> saves = interactionRepository.findByUserIdAndInteractionType(userId, InteractionType.SAVE, page);

        Map<String, PostDto> allPosts = saves.stream().map(like -> localPostCache.getFromCache(like.getPostId()))
                .collect(Collectors.toMap(PostDto::getPostId, post -> post));

        PaginationInfoDto paginationInfo = new PaginationInfoDto(saves.getTotalPages(), searchParameters.getPage(), saves.getTotalElements(), !saves.hasNext());
        return userInteractionsDtoMapper.toDto(optionalLocalUser.get(), InteractionType.SAVE, saves.getContent(), allPosts, paginationInfo);
    }
}
