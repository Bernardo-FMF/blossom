package org.blossom.activity.kafka;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.blossom.activity.cache.LocalPostCacheService;
import org.blossom.activity.mapper.impl.LocalPostDtoMapper;
import org.blossom.activity.repository.CommentRepository;
import org.blossom.activity.repository.InteractionRepository;
import org.blossom.facade.KafkaResourceHandler;
import org.blossom.model.KafkaPostResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
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
        log.info("processing save message of type post: {}", resource);

        localPostCache.addToCache(resource.getId(), localPostDtoMapper.toDto(resource));
    }

    @Override
    public void update(KafkaPostResource resource) {
        log.info("discarding update message of type post: {}", resource);
    }

    @Override
    @Transactional
    public void delete(KafkaPostResource resource) {
        log.info("processing delete message of type post: {}", resource);

        localPostCache.deleteCacheEntry(resource.getId());
        interactionRepository.deleteByPostId(resource.getId());
        commentRepository.deleteByPostId(resource.getId());
    }
}
