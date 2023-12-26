package org.blossom.post.kafka.inbound;

import org.apache.commons.lang.NotImplementedException;
import org.blossom.post.cache.LocalUserCacheService;
import org.blossom.facade.KafkaResourceHandler;
import org.blossom.model.KafkaUserResource;
import org.blossom.post.mapper.impl.UserMapper;
import org.blossom.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserResourceHandler implements KafkaResourceHandler<KafkaUserResource> {
    @Autowired
    private LocalUserCacheService localUserCache;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostRepository postRepository;

    @Override
    public void save(KafkaUserResource resource) {
        localUserCache.addToCache(String.valueOf(resource.getId()), userMapper.toDto(resource));
    }

    @Override
    public void update(KafkaUserResource resource) {
        localUserCache.updateCacheEntry(String.valueOf(resource.getId()), userMapper.toDto(resource));
    }

    @Override
    public void delete(KafkaUserResource resource) {
        throw new NotImplementedException("User deletes are not available");
    }
}