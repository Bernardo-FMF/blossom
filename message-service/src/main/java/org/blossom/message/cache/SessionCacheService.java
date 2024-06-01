package org.blossom.message.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class SessionCacheService {
    private static final String PREFIX = "message-session-id";

    @Autowired
    private RedisTemplate<String, String[]> redisTemplate;

    public String[] getFromCache(Integer key) {
        String concatKey = PREFIX + key;
        return redisTemplate.opsForValue().get(concatKey);
    }

    public void addToCache(Integer key, String value) {
        String[] cachedIds = getFromCache(key);
        String concatKey = PREFIX + key;
        if (cachedIds == null) {
            redisTemplate.opsForValue().set(concatKey, new String[] {value});
        } else {
            String[] newIds = Arrays.copyOf(cachedIds, cachedIds.length + 1);
            newIds[newIds.length - 1] = value;
            redisTemplate.opsForValue().set(concatKey, newIds);
        }
    }

    public void deleteFromCache(Integer key, String value) {
        String concatKey = PREFIX + key;
        String[] cachedIds = getFromCache(key);

        String[] newIds = Arrays.stream(cachedIds).filter(id -> !id.equals(value)).toArray(String[]::new);
        if (newIds.length == 0) {
            redisTemplate.delete(concatKey);
        } else {
            redisTemplate.opsForValue().set(concatKey, newIds);
        }
    }
}
