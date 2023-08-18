package org.blossom.service;

import org.blossom.cache.LocalUserCacheService;
import org.blossom.dto.PostInfoDto;
import org.blossom.entity.Post;
import org.blossom.exception.PostNotValidException;
import org.blossom.exception.UserNotFoundException;
import org.blossom.grpc.GrpcClientImageService;
import org.blossom.mapper.PostDtoMapper;
import org.blossom.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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