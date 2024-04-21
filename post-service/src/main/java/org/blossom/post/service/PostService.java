package org.blossom.post.service;

import com.google.common.base.Strings;
import org.blossom.post.cache.LocalUserCacheService;
import org.blossom.post.dto.*;
import org.blossom.post.entity.Post;
import org.blossom.post.exception.*;
import org.blossom.post.factory.impl.PostFactory;
import org.blossom.post.grpc.service.GrpcClientActivityService;
import org.blossom.post.grpc.service.GrpcClientImageService;
import org.blossom.post.kafka.outbound.KafkaMessageService;
import org.blossom.post.mapper.impl.*;
import org.blossom.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LocalUserCacheService localUserCache;

    @Autowired
    private GrpcClientImageService imageService;

    @Autowired
    private PostFactory postFactory;

    @Autowired
    private KafkaMessageService messageService;

    @Autowired
    private PostDtoMapper postDtoMapper;

    @Autowired
    private PostUserDtoMapper postUserDtoMapper;

    @Autowired
    private GenericDtoMapper genericDtoMapper;

    @Autowired
    private AggregatePostsDtoMapper aggregatePostsDtoMapper;

    @Autowired
    private PostIdentifierDtoMapper postIdentifierDtoMapper;

    @Autowired
    private AggregateUserPostsDtoMapper aggregateUserPostsDtoMapper;

    @Autowired
    private GrpcClientActivityService grpcClientActivityService;

    public GenericResponseDto createPost(PostInfoDto postInfoDto, int userId) throws IOException, InterruptedException, PostNotValidException, FileUploadException, UserNotFoundException {
        UserDto user = localUserCache.getFromCache(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        if (Objects.isNull(postInfoDto.getMediaFiles()) && Strings.isNullOrEmpty(postInfoDto.getText())) {
            throw new PostNotValidException("Post has no content");
        }

        if (!Objects.isNull(postInfoDto.getMediaFiles()) && postInfoDto.getMediaFiles().length != 0) {
            postInfoDto.setMediaUrls(imageService.uploadImages(postInfoDto.getMediaFiles()));
        }

        if (!Strings.isNullOrEmpty(postInfoDto.getText())) {
            postInfoDto.setHashtags(parseDescription(postInfoDto.getText()));
        }

        postInfoDto.setUserId(userId);

        Post post = postFactory.buildEntity(postInfoDto);

        Post newPost = postRepository.save(post);

        messageService.publishCreation(newPost);

        return genericDtoMapper.toDto("Post created successfully", newPost.getId(), null);
    }

    public GenericResponseDto deletePost(String postId, int userId) throws PostNotFoundException, OperationNotAllowedException, FileDeleteException, UserNotFoundException {
        UserDto user = localUserCache.getFromCache(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new PostNotFoundException("Post does not exist");
        }

        Post post = optionalPost.get();

        if (post.getUserId() != userId) {
            throw new OperationNotAllowedException("Logged in user cannot delete this post");
        }

        if (post.getMedia().length != 0) {
            imageService.deleteImages(post.getMedia());
        }

        postRepository.delete(post);

        messageService.publishDelete(post);

        return genericDtoMapper.toDto("Post deleted successfully", postId, null);
    }

    public AggregateUserPostsDto findByUser(Integer userId, SearchParametersDto searchParameters, Integer authUserId) throws InterruptedException {
        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit(), Sort.by(Sort.Direction.DESC, "createdAt")) : null;

        Page<Post> posts = postRepository.findByUserId(userId, page);

        Map<String, MetadataDto> metadata = grpcClientActivityService.getMetadata(authUserId, posts.stream().map(Post::getId).distinct().collect(Collectors.toList()));

        return aggregateUserPostsDtoMapper.toPaginatedDto(
                posts.getContent(),
                userId,
                metadata,
                aggregateUserPostsDtoMapper.createPaginationInfo(posts.getNumber(), posts.getTotalPages(), posts.getTotalElements(), !posts.hasNext()));
    }

    public PostIdentifierDto getPostIdentifier(String postId) throws PostNotFoundException {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new PostNotFoundException("Post does not exist");
        }

        Post post = optionalPost.get();

        return postIdentifierDtoMapper.toDto(post);
    }

    public PostDto getPost(String postId, Integer userId) throws PostNotFoundException, UserNotFoundException, InterruptedException {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new PostNotFoundException("Post does not exist");
        }

        Post post = optionalPost.get();

        UserDto user = localUserCache.getFromCache(post.getUserId());
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        Map<String, MetadataDto> metadata = grpcClientActivityService.getMetadata(userId, List.of(postId));

        return postDtoMapper.toDto(post, metadata.get(postId));
    }

    private String[] parseDescription(String text) {
        List<String> hashtags = new ArrayList<>();

        Pattern pattern = Pattern.compile("#\\w+");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String hashtag = matcher.group();
            hashtags.add(hashtag.substring(1));
        }

        return hashtags.toArray(new String[0]);
    }
}