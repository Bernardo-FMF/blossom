package org.blossom.activity.service;

import org.blossom.activity.cache.LocalPostCacheService;
import org.blossom.activity.dto.*;
import org.blossom.activity.entity.Interaction;
import org.blossom.activity.entity.LocalUser;
import org.blossom.activity.enums.InteractionType;
import org.blossom.activity.exception.*;
import org.blossom.activity.dto.PostDto;
import org.blossom.activity.mapper.InteractionDtoMapper;
import org.blossom.activity.mapper.InteractionMapper;
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
    private InteractionMapper interactionMapper;

    @Autowired
    private InteractionDtoMapper interactionDtoMapper;

    @Autowired
    private LocalUserRepository localUserRepository;

    public GenericCreationDto createLike(InteractionInfoDto interactionInfoDto, int userId) throws OperationNotAllowedException, UserNotFoundException, PostNotFoundException, InteractionAlreadyExistsException {
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

        Interaction interaction = interactionMapper.mapToInteraction(interactionInfoDto, optionalLocalUser.get(), InteractionType.LIKE);

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

        Interaction interaction = interactionMapper.mapToInteraction(interactionInfoDto, optionalLocalUser.get(), InteractionType.SAVE);

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

        if (interaction.getUser().getId() != userId) {
            throw new OperationNotAllowedException("Logged in user cannot perform this operation");
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

        if (interaction.getUser().getId() != userId) {
            throw new OperationNotAllowedException("Logged in user cannot perform this operation");
        }

        if (!localPostCache.findEntry(interaction.getPostId())) {
            throw new PostNotFoundException("Post not found");
        }

        interactionRepository.deleteById(interactionId);

        return "Save was deleted successfully";
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

        return UserInteractionsDto.builder()
                .user(optionalLocalUser.get())
                .interactionType(InteractionType.LIKE)
                .interactions(likes.get().map(interaction -> interactionDtoMapper.mapToInteractionDto(interaction, allPosts.get(interaction.getPostId()))).toList())
                .totalPages(likes.getTotalPages())
                .currentPage(searchParameters.getPage())
                .totalElements(likes.getTotalElements())
                .eof(!likes.hasNext())
                .build();
    }

    public UserInteractionsDto getUserSaves(SearchParametersDto searchParameters, int userId) throws UserNotFoundException {
        Optional<LocalUser> optionalLocalUser = localUserRepository.findById(userId);
        if (optionalLocalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit()) : null;

        Page<Interaction> likes = interactionRepository.findByUserIdAndInteractionType(userId, InteractionType.SAVE, page);

        Map<String, PostDto> allPosts = likes.stream().map(like -> localPostCache.getFromCache(like.getPostId()))
                .collect(Collectors.toMap(PostDto::getPostId, post -> post));

        return UserInteractionsDto.builder()
                .user(optionalLocalUser.get())
                .interactionType(InteractionType.SAVE)
                .interactions(likes.get().map(interaction -> interactionDtoMapper.mapToInteractionDto(interaction, allPosts.get(interaction.getPostId()))).toList())
                .totalPages(likes.getTotalPages())
                .currentPage(searchParameters.getPage())
                .totalElements(likes.getTotalElements())
                .eof(!likes.hasNext())
                .build();
    }

    public InteractionDto findSave(String postId, int userId) throws PostNotFoundException, UserNotFoundException {
        Optional<LocalUser> optionalLocalUser = localUserRepository.findById(userId);
        if (optionalLocalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        PostDto postDto = localPostCache.getFromCache(postId);
        if (postDto == null) {
            throw new PostNotFoundException("Post not found");
        }

        Optional<Interaction> optionalInteraction = interactionRepository.findByUserIdAndPostIdAndInteractionType(userId, postId, InteractionType.SAVE);
        return optionalInteraction.map(interaction -> interactionDtoMapper.mapToInteractionDto(interaction, postDto)).orElse(null);
    }

    public InteractionDto findLike(String postId, int userId) throws PostNotFoundException, UserNotFoundException {
        Optional<LocalUser> optionalLocalUser = localUserRepository.findById(userId);
        if (optionalLocalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        PostDto postDto = localPostCache.getFromCache(postId);
        if (postDto == null) {
            throw new PostNotFoundException("Post not found");
        }

        Optional<Interaction> optionalInteraction = interactionRepository.findByUserIdAndPostIdAndInteractionType(userId, postId, InteractionType.LIKE);
        return optionalInteraction.map(interaction -> interactionDtoMapper.mapToInteractionDto(interaction, postDto)).orElse(null);
    }
}
