package org.blossom.cache;

import org.blossom.kafka.inbound.model.LocalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class LocalUserCacheService {
    @Autowired
    private RedisTemplate<String, LocalUser> redisTemplate;

    public LocalUser getFromCache(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void addToCache(String key, LocalUser value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void updateCacheEntry(String key, LocalUser newValue) {
        redisTemplate.opsForValue().set(key, newValue);
    }

    public List<LocalUser> getMultiFromCache(Collection<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }
}
