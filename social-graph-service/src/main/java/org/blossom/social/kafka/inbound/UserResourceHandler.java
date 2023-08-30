package org.blossom.social.kafka.inbound;

import org.apache.commons.lang.NotImplementedException;
import org.blossom.social.cache.LocalUserCacheService;
import org.blossom.social.entity.GraphUser;
import org.blossom.facade.KafkaResourceHandler;
import org.blossom.social.mapper.LocalUserMapper;
import org.blossom.model.KafkaUserResource;
import org.blossom.social.repository.SocialRepository;
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
        throw new NotImplementedException("User deletes are not available");
    }
}