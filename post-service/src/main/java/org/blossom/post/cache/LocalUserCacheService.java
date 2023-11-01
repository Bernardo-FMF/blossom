package org.blossom.post.cache;

import org.blossom.post.client.UserClient;
import org.blossom.post.kafka.inbound.model.LocalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Service
public class LocalUserCacheService {
    @Autowired
    private RedisTemplate<String, LocalUser> redisTemplate;

    @Autowired
    private UserClient userClient;

    public LocalUser getFromCache(Integer key) {
        LocalUser localUser = redisTemplate.opsForValue().get(String.valueOf(key));
        if (localUser == null) {
            ResponseEntity<LocalUser> userResponse = userClient.getUserById(key);
            if (userResponse.getStatusCode().is2xxSuccessful()) {
                localUser = userResponse.getBody();
                addToCache(String.valueOf(key), localUser);
            }
        }
        return localUser;
    }


    public void addToCache(String key, LocalUser value) {
        redisTemplate.opsForValue().set(key, value, Duration.of(1, ChronoUnit.DAYS));
    }

    public void updateCacheEntry(String key, LocalUser newValue) {
        redisTemplate.opsForValue().set(key, newValue, Duration.of(1, ChronoUnit.DAYS));
    }

}
