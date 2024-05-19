package org.blossom.post.kafka.inbound;

import lombok.extern.log4j.Log4j2;
import org.blossom.facade.KafkaResourceHandler;
import org.blossom.model.KafkaUserResource;
import org.blossom.post.cache.LocalUserCacheService;
import org.blossom.post.entity.Post;
import org.blossom.post.kafka.outbound.KafkaMessageService;
import org.blossom.post.mapper.impl.UserDtoMapper;
import org.blossom.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class UserResourceHandler implements KafkaResourceHandler<KafkaUserResource> {
    @Autowired
    private LocalUserCacheService localUserCache;

    @Autowired
    private UserDtoMapper userDtoMapper;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private KafkaMessageService messageService;

    @Override
    public void save(KafkaUserResource resource) {
        log.info("processing save message of type user: {}", resource);

        localUserCache.addToCache(String.valueOf(resource.getId()), userDtoMapper.toDto(resource));
    }

    @Override
    public void update(KafkaUserResource resource) {
        log.info("processing update message of type user: {}", resource);

        localUserCache.updateCacheEntry(String.valueOf(resource.getId()), userDtoMapper.toDto(resource));
    }

    @Override
    @Transactional
    public void delete(KafkaUserResource resource) {
        log.info("processing delete message of type user: {}", resource);

        localUserCache.deleteFromCache(String.valueOf(resource.getId()));

        Page<Post> allUserPosts = postRepository.findByUserId(resource.getId(), Pageable.unpaged());
        postRepository.deleteAll(allUserPosts);

        for (Post post: allUserPosts.getContent()) {
            messageService.publishDelete(post);
        }
    }
}