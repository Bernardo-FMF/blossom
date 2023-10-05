package org.blossom.feed.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class FeedCacheService {
    private static final String GENERIC_FEED_CACHE = "generic-feed";

    @Autowired
    private RedisTemplate<String, List<String>> redisTemplate;

    public List<String> getFromCache() {
        return redisTemplate.opsForValue().get(GENERIC_FEED_CACHE);
    }

    public void addToCache(List<String> postIds) {
        redisTemplate.opsForValue().set(GENERIC_FEED_CACHE, postIds, Duration.of(1, ChronoUnit.HALF_DAYS));
    }

    public void updateCache(List<String> postIds) {
        redisTemplate.opsForValue().set(GENERIC_FEED_CACHE, postIds, Duration.of(1, ChronoUnit.HALF_DAYS));
    }

    public void invalidateCache(String postId) {
        List<String> ids = redisTemplate.opsForValue().get(GENERIC_FEED_CACHE);
        if (ids != null && !ids.isEmpty() && ids.contains(postId)) {
            ids.removeIf(id -> id.equals(postId));
            updateCache(ids);
        }
    }
}
