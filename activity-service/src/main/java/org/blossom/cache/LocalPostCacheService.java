package org.blossom.cache;

import org.blossom.kafka.model.LocalPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class LocalPostCacheService {
    @Autowired
    @Qualifier("redisTemplatePost")
    private RedisTemplate<String, LocalPost> redisTemplatePost;

    public void addToCache(String key, LocalPost value) {
        redisTemplatePost.opsForValue().set(key, value);
    }

    public void updateCacheEntry(String key, LocalPost newValue) {
        redisTemplatePost.opsForValue().set(key, newValue);
    }

    public LocalPost getFromCache(String key) {
        return redisTemplatePost.opsForValue().get(key);
    }

    public List<LocalPost> getMultiFromCache(Collection<String> keys) {
        return redisTemplatePost.opsForValue().multiGet(keys);
    }

    public void deleteCacheEntry(String s) {
        redisTemplatePost.delete(s);
    }

    public boolean findEntry(String key) {
        return getFromCache(key) != null;
    }
}
