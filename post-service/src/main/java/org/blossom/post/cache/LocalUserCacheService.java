package org.blossom.post.cache;

import org.blossom.post.client.UserClient;
import org.blossom.post.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Service
public class LocalUserCacheService {
    @Autowired
    private RedisTemplate<String, UserDto> redisTemplate;

    @Autowired
    private UserClient userClient;

    public UserDto getFromCache(Integer key) {
        UserDto userDto = redisTemplate.opsForValue().get(String.valueOf(key));
        if (userDto == null) {
            ResponseEntity<UserDto> userResponse = userClient.getUserById(key);
            if (userResponse.getStatusCode().is2xxSuccessful()) {
                userDto = userResponse.getBody();
                addToCache(String.valueOf(key), userDto);
            }
        }
        return userDto;
    }


    public void addToCache(String key, UserDto value) {
        redisTemplate.opsForValue().set(key, value, Duration.of(1, ChronoUnit.DAYS));
    }

    public void updateCacheEntry(String key, UserDto newValue) {
        redisTemplate.opsForValue().set(key, newValue, Duration.of(1, ChronoUnit.DAYS));
    }

    public void deleteFromCache(String key) {
        redisTemplate.delete(key);
    }
}
