package org.blossom.cache;

import org.blossom.kafka.model.LocalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class LocalUserCacheService {
    @Autowired
    @Qualifier("redisTemplateUser")
    private RedisTemplate<String, LocalUser> redisTemplateUser;

    public void addToCache(String key, LocalUser value) {
        redisTemplateUser.opsForValue().set(key, value);
    }

    public void updateCacheEntry(String key, LocalUser newValue) {
        redisTemplateUser.opsForValue().set(key, newValue);
    }

    public LocalUser getFromCache(String key) {
        return redisTemplateUser.opsForValue().get(key);
    }

    public List<LocalUser> getMultiFromCache(Collection<String> keys) {
        return redisTemplateUser.opsForValue().multiGet(keys);
    }

    public void deleteCacheEntry(String s) {
        redisTemplateUser.delete(s);
    }

    public boolean findEntry(String key) {
        return getFromCache(key) != null;
    }
}