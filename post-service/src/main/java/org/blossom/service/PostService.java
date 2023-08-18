package org.blossom.service;

import org.blossom.cache.LocalUserCacheService;
import org.blossom.dto.AggregateUserPostsDto;
import org.blossom.dto.PostInfoDto;
import org.blossom.dto.SearchParametersDto;
import org.blossom.entity.Post;
import org.blossom.exception.PostNotFoundException;
import org.blossom.exception.PostNotValidException;
import org.blossom.exception.UserNotFoundException;
import org.blossom.grpc.GrpcClientImageService;
import org.blossom.kafka.inbound.model.LocalUser;
import org.blossom.mapper.PostDtoMapper;
import org.blossom.repository.PostRepository;
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
    private LocalUserCacheService localUserCacheService;

    @Autowired
    private GrpcClientImageService imageService;

    @Autowired
    private PostDtoMapper postDtoMapper;

    public String createPost(PostInfoDto postInfoDto, int userId) throws UserNotFoundException, IOException, InterruptedException, PostNotValidException {
        if (postInfoDto.getUserId() != userId || localUserCacheService.findEntry(String.valueOf(userId))) {
            throw new UserNotFoundException("User not found");
        }

        if (postInfoDto.getMediaFiles().length == 0 && postInfoDto.getText().isEmpty()) {
            throw new PostNotValidException("Post has no content");
        }

        String[] mediaUrls = imageService.uploadImages(postInfoDto.getMediaFiles());
        String[] hashtags = parseDescription(postInfoDto.getText());

        Post post = postDtoMapper.mapToPost(postInfoDto, mediaUrls, hashtags);

        Post newPost = postRepository.save(post);

        return newPost.getId();
    }

    public String deletePost(String postId) throws PostNotFoundException {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new PostNotFoundException("Post does not exist");
        }

        Post post = optionalPost.get();

        if (post.getMedia().length != 0) {
            imageService.deleteImages(post.getMedia());
        }

        postRepository.delete(post);

        return "Post was deleted successfully";
    }

    public AggregateUserPostsDto findByUser(Integer userId, SearchParametersDto searchParameters) {
        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit()) : null;

        LocalUser user = localUserCacheService.getFromCache(String.valueOf(userId));

        Page<Post> posts = postRepository.findByUserId(userId, page);

        return AggregateUserPostsDto.builder()
                .posts(posts.get().map(post -> postDtoMapper.mapToPostDto(post)).collect(Collectors.toList()))
                .user(user)
                .currentPage(posts.getNumber())
                .totalPages(posts.getTotalPages())
                .totalElements(posts.getTotalElements())
                .eof(!posts.hasNext())
                .build();
    }

    private String[] parseDescription(String text) {
        List<String> hashtags = new ArrayList<>();

        Pattern pattern = Pattern.compile("#\\w+");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String hashtag = matcher.group();
            hashtags.add(hashtag);
        }

        return hashtags.toArray(new String[0]);
    }
}