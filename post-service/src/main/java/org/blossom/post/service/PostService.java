package org.blossom.post.service;

import com.google.common.base.Strings;
import org.blossom.post.cache.LocalUserCacheService;
import org.blossom.post.dto.*;
import org.blossom.post.entity.Post;
import org.blossom.post.exception.*;
import org.blossom.post.grpc.GrpcClientImageService;
import org.blossom.post.kafka.inbound.model.LocalUser;
import org.blossom.post.kafka.outbound.KafkaMessageService;
import org.blossom.post.mapper.PostDtoMapper;
import org.blossom.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private PostDtoMapper postDtoMapper;

    @Autowired
    private KafkaMessageService messageService;

    public String createPost(PostInfoDto postInfoDto, int userId) throws IOException, InterruptedException, PostNotValidException, FileUploadException {
        if (postInfoDto.getMediaFiles().length == 0 && postInfoDto.getText().isEmpty()) {
            throw new PostNotValidException("Post has no content");
        }

        String[] mediaUrls = null;
        if (postInfoDto.getMediaFiles().length != 0) {
            mediaUrls = imageService.uploadImages(postInfoDto.getMediaFiles());
        }

        String[] hashtags = null;
        if (Strings.isNullOrEmpty(postInfoDto.getText())) {
            hashtags = parseDescription(postInfoDto.getText());
        }

        Post post = postDtoMapper.mapToPost(postInfoDto, userId, mediaUrls, hashtags);

        Post newPost = postRepository.save(post);

        messageService.publishCreation(newPost);

        return newPost.getId();
    }

    public String deletePost(String postId, int userId) throws PostNotFoundException, OperationNotAllowedException, FileDeleteException {
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

        return "Post was deleted successfully";
    }

    public AggregateUserPostsDto findByUser(Integer userId, SearchParametersDto searchParameters) {
        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit()) : null;

        Page<Post> posts = postRepository.findByUserId(userId, page);

        return AggregateUserPostsDto.builder()
                .posts(posts.get().map(post -> postDtoMapper.mapToPostDto(post)).collect(Collectors.toList()))
                .userId(userId)
                .currentPage(posts.getNumber())
                .totalPages(posts.getTotalPages())
                .totalElements(posts.getTotalElements())
                .eof(!posts.hasNext())
                .build();
    }

    public PostIdentifierDto getPostIdentifier(String postId) throws PostNotFoundException {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new PostNotFoundException("Post does not exist");
        }

        Post post = optionalPost.get();

        return PostIdentifierDto.builder()
                .postId(post.getId())
                .userId(post.getUserId())
                .build();
    }

    public PostWithUserDto getPost(String postId) throws PostNotFoundException, UserNotFoundException {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new PostNotFoundException("Post does not exist");
        }

        Post post = optionalPost.get();

        LocalUser user = localUserCache.getFromCache(post.getUserId());
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        return postDtoMapper.mapToPostWithUserDto(post, user);
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