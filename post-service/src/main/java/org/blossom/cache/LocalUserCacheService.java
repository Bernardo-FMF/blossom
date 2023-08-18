package org.blossom.cache;

import org.blossom.kafka.inbound.model.LocalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.List;

@Service
public class LocalUserCacheService {
    @Autowired
    private RedisTemplate<String, LocalUser> redisTemplate;

    @Autowired
    private WebClient.Builder webClient;

    public void addToCache(String key, LocalUser value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void updateCacheEntry(String key, LocalUser newValue) {
        redisTemplate.opsForValue().set(key, newValue);
    }

    public LocalUser getFromCache(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public List<LocalUser> getMultiFromCache(Collection<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    public void deleteCacheEntry(String s) {
        redisTemplate.delete(s);
    }

    public boolean findEntry(String key) {
        return getFromCache(key) != null;
    }
}
