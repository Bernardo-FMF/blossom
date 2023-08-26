package org.blossom.service;

import org.blossom.cache.LocalPostCacheService;
import org.blossom.cache.LocalUserCacheService;
import org.blossom.dto.*;
import org.blossom.entity.Interaction;
import org.blossom.enums.InteractionType;
import org.blossom.exception.*;
import org.blossom.mapper.InteractionDtoMapper;
import org.blossom.mapper.InteractionMapper;
import org.blossom.repository.InteractionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InteractionService {
    @Autowired
    private InteractionRepository interactionRepository;

    @Autowired
    private LocalUserCacheService localUserCache;

    @Autowired
    private LocalPostCacheService localPostCache;

    @Autowired
    private InteractionMapper interactionMapper;

    @Autowired
    private InteractionDtoMapper interactionDtoMapper;

    public GenericCreationDto createLike(InteractionInfoDto interactionInfoDto, int userId) throws OperationNotAllowedException, UserNotFoundException, PostNotFoundException, InteractionAlreadyExistsException {
        if (interactionInfoDto.getUserId() != userId) {
            throw new OperationNotAllowedException("Logged in user cannot perform this operation");
        }

        if (!localUserCache.findEntry(String.valueOf(userId))) {
            throw new UserNotFoundException("User not found");
        }

        if (!localPostCache.findEntry(interactionInfoDto.getPostId())) {
            throw new PostNotFoundException("Post not found");
        }

        if (interactionRepository.existsByUserIdAndPostIdAndInteractionType(userId, interactionInfoDto.getPostId(), InteractionType.LIKE)) {
            throw new InteractionAlreadyExistsException("Like already exists");
        }

        Interaction interaction = interactionMapper.mapToInteraction(interactionInfoDto, InteractionType.LIKE);

        Interaction newInteraction = interactionRepository.save(interaction);

        return GenericCreationDto.builder()
                .id(newInteraction.getId())
                .message("Like was created successfully")
                .build();
    }

    public GenericCreationDto createSave(InteractionInfoDto interactionInfoDto, int userId) throws OperationNotAllowedException, UserNotFoundException, PostNotFoundException, InteractionAlreadyExistsException {
        if (interactionInfoDto.getUserId() != userId) {
            throw new OperationNotAllowedException("Logged in user cannot perform this operation");
        }

        if (!localUserCache.findEntry(String.valueOf(userId))) {
            throw new UserNotFoundException("User not found");
        }

        if (!localPostCache.findEntry(interactionInfoDto.getPostId())) {
            throw new PostNotFoundException("Post not found");
        }

        if (interactionRepository.existsByUserIdAndPostIdAndInteractionType(userId, interactionInfoDto.getPostId(), InteractionType.SAVE)) {
            throw new InteractionAlreadyExistsException("Save already exists");
        }

        Interaction interaction = interactionMapper.mapToInteraction(interactionInfoDto, InteractionType.SAVE);

        Interaction newInteraction = interactionRepository.save(interaction);

        return GenericCreationDto.builder()
                .id(newInteraction.getId())
                .message("Like was created successfully")
                .build();
    }

    public String deleteLike(Integer interactionId, int userId) throws InteractionNotFoundException, OperationNotAllowedException, UserNotFoundException, PostNotFoundException {
        Optional<Interaction> optionalInteraction = interactionRepository.findById(interactionId);
        if (optionalInteraction.isEmpty()) {
            throw new InteractionNotFoundException("Like not found");
        }

        Interaction interaction = optionalInteraction.get();

        if (interaction.getUserId() != userId) {
            throw new OperationNotAllowedException("Logged in user cannot perform this operation");
        }

        if (!localUserCache.findEntry(String.valueOf(userId))) {
            throw new UserNotFoundException("User not found");
        }

        if (!localPostCache.findEntry(interaction.getPostId())) {
            throw new PostNotFoundException("Post not found");
        }

        interactionRepository.deleteById(interactionId);

        return "Like was deleted successfully";
    }

    public String deleteSave(Integer interactionId, int userId) throws InteractionNotFoundException, OperationNotAllowedException, UserNotFoundException, PostNotFoundException {
        Optional<Interaction> optionalInteraction = interactionRepository.findById(interactionId);
        if (optionalInteraction.isEmpty()) {
            throw new InteractionNotFoundException("Save not found");
        }

        Interaction interaction = optionalInteraction.get();

        if (interaction.getUserId() != userId) {
            throw new OperationNotAllowedException("Logged in user cannot perform this operation");
        }

        if (!localUserCache.findEntry(String.valueOf(userId))) {
            throw new UserNotFoundException("User not found");
        }

        if (!localPostCache.findEntry(interaction.getPostId())) {
            throw new PostNotFoundException("Post not found");
        }

        interactionRepository.deleteById(interactionId);

        return "Save was deleted successfully";
    }

    public UserInteractionsDto getUserLikes(SearchParametersDto searchParameters, int userId) throws UserNotFoundException {
        if (!localUserCache.findEntry(String.valueOf(userId))) {
            throw new UserNotFoundException("User not found");
        }

        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit()) : null;

        Page<Interaction> likes = interactionRepository.findByUserIdAndInteractionType(userId, InteractionType.LIKE, page);

        return UserInteractionsDto.builder()
                .user(localUserCache.getFromCache(String.valueOf(userId)))
                .interactionType(InteractionType.LIKE)
                .interactions(likes.get().map(interaction -> interactionDtoMapper.mapToInteractionDto(interaction)).toList())
                .totalPages(likes.getTotalPages())
                .currentPage(searchParameters.getPage())
                .totalElements(likes.getTotalElements())
                .eof(!likes.hasNext())
                .build();
    }

    public UserInteractionsDto getUserSaves(SearchParametersDto searchParameters, int userId) throws UserNotFoundException {
        if (!localUserCache.findEntry(String.valueOf(userId))) {
            throw new UserNotFoundException("User not found");
        }

        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit()) : null;

        Page<Interaction> likes = interactionRepository.findByUserIdAndInteractionType(userId, InteractionType.SAVE, page);

        return UserInteractionsDto.builder()
                .user(localUserCache.getFromCache(String.valueOf(userId)))
                .interactionType(InteractionType.SAVE)
                .interactions(likes.get().map(interaction -> interactionDtoMapper.mapToInteractionDto(interaction)).toList())
                .totalPages(likes.getTotalPages())
                .currentPage(searchParameters.getPage())
                .totalElements(likes.getTotalElements())
                .eof(!likes.hasNext())
                .build();
    }

    public InteractionDto findSave(String postId, int userId) throws UserNotFoundException, PostNotFoundException {
        if (!localUserCache.findEntry(String.valueOf(userId))) {
            throw new UserNotFoundException("User not found");
        }

        if (!localPostCache.findEntry(postId)) {
            throw new PostNotFoundException("Post not found");
        }

        Optional<Interaction> optionalInteraction = interactionRepository.findByUserIdAndPostIdAndInteractionType(userId, postId, InteractionType.SAVE);
        return optionalInteraction.map(interaction -> interactionDtoMapper.mapToInteractionDto(interaction)).orElse(null);
    }

    public InteractionDto findLike(String postId, int userId) throws UserNotFoundException, PostNotFoundException {
        if (!localUserCache.findEntry(String.valueOf(userId))) {
            throw new UserNotFoundException("User not found");
        }

        if (!localPostCache.findEntry(postId)) {
            throw new PostNotFoundException("Post not found");
        }

        Optional<Interaction> optionalInteraction = interactionRepository.findByUserIdAndPostIdAndInteractionType(userId, postId, InteractionType.LIKE);
        return optionalInteraction.map(interaction -> interactionDtoMapper.mapToInteractionDto(interaction)).orElse(null);
    }
}
