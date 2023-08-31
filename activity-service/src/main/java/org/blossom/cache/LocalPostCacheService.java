package org.blossom.cache;

import org.blossom.client.PostClient;
import org.blossom.kafka.model.LocalPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Service
public class LocalPostCacheService {
    @Autowired
    private RedisTemplate<String, LocalPost> redisTemplatePost;

    @Autowired
    private PostClient postClient;

    public void addToCache(String key, LocalPost value) {
        redisTemplatePost.opsForValue().set(key, value, Duration.of(3, ChronoUnit.DAYS));
    }

    public LocalPost getFromCache(String key) {
        LocalPost localPost = redisTemplatePost.opsForValue().get(key);
        if (localPost == null) {
            ResponseEntity<LocalPost> postIdentifier = postClient.getPostIdentifier(key);
            if (postIdentifier.getStatusCode().is2xxSuccessful()) {
                localPost = postIdentifier.getBody();
                addToCache(key, localPost);
            }
        }
        return localPost;
    }

    public void deleteCacheEntry(String s) {
        redisTemplatePost.delete(s);
    }

    public boolean findEntry(String key) {
        return getFromCache(key) != null;
    }
}
