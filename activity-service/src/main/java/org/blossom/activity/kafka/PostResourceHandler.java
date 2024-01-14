package org.blossom.activity.kafka;

import org.apache.commons.lang.NotImplementedException;
import org.blossom.activity.cache.LocalPostCacheService;
import org.blossom.facade.KafkaResourceHandler;
import org.blossom.activity.mapper.impl.LocalPostDtoMapper;
import org.blossom.model.KafkaPostResource;
import org.blossom.activity.repository.CommentRepository;
import org.blossom.activity.repository.InteractionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostResourceHandler implements KafkaResourceHandler<KafkaPostResource> {
    @Autowired
    private LocalPostCacheService localPostCache;

    @Autowired
    private LocalPostDtoMapper localPostDtoMapper;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private InteractionRepository interactionRepository;

    @Override
    public void save(KafkaPostResource resource) {
        localPostCache.addToCache(resource.getId(), localPostDtoMapper.toDto(resource));
    }

    @Override
    public void update(KafkaPostResource resource) {
        throw new NotImplementedException("Post updates are not available");
    }

    @Override
    public void delete(KafkaPostResource resource) {
        localPostCache.deleteCacheEntry(resource.getId());
        interactionRepository.deleteByPostId(resource.getId());
        commentRepository.deleteByPostId(resource.getId());
    }
}
