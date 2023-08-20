package org.blossom.kafka.inbound;

import org.blossom.cache.LocalUserCacheService;
import org.blossom.entity.GraphUser;
import org.blossom.facade.KafkaResourceHandler;
import org.blossom.mapper.LocalUserMapper;
import org.blossom.model.KafkaUserResource;
import org.blossom.repository.SocialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserResourceHandler implements KafkaResourceHandler<KafkaUserResource> {
    @Autowired
    private LocalUserCacheService localUserCache;

    @Autowired
    private LocalUserMapper localUserMapper;

    @Autowired
    private SocialRepository socialRepository;

    @Override
    public void save(KafkaUserResource resource) {
        localUserCache.addToCache(String.valueOf(resource.getId()), localUserMapper.mapToLocalUser(resource));
        socialRepository.save(GraphUser.builder().userId(resource.getId()).build());
    }

    @Override
    public void update(KafkaUserResource resource) {
        localUserCache.updateCacheEntry(String.valueOf(resource.getId()), localUserMapper.mapToLocalUser(resource));
    }

    @Override
    public void delete(KafkaUserResource resource) {
        localUserCache.deleteCacheEntry(String.valueOf(resource.getId()));
        socialRepository.deleteById(resource.getId());
    }
}