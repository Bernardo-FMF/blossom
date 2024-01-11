package org.blossom.activity.cache;

import org.blossom.activity.client.PostClient;
import org.blossom.activity.dto.PostDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Service
public class LocalPostCacheService {
    @Autowired
    private RedisTemplate<String, PostDto> redisTemplatePost;

    @Autowired
    private PostClient postClient;

    public void addToCache(String key, PostDto value) {
        redisTemplatePost.opsForValue().set(key, value, Duration.of(3, ChronoUnit.DAYS));
    }

    public PostDto getFromCache(String key) {
        PostDto postDto = redisTemplatePost.opsForValue().get(key);
        if (postDto == null) {
            ResponseEntity<PostDto> postIdentifier = postClient.getPostIdentifier(key);
            if (postIdentifier.getStatusCode().is2xxSuccessful()) {
                postDto = postIdentifier.getBody();
                addToCache(key, postDto);
            }
        }
        return postDto;
    }

    public void deleteCacheEntry(String s) {
        redisTemplatePost.delete(s);
    }

    public boolean findEntry(String key) {
        return getFromCache(key) != null;
    }
}
