package org.blossom.social.kafka.inbound;

import org.blossom.facade.KafkaResourceHandler;
import org.blossom.model.KafkaUserResource;
import org.blossom.social.entity.GraphUser;
import org.blossom.social.mapper.LocalUserMapper;
import org.blossom.social.repository.SocialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserResourceHandler implements KafkaResourceHandler<KafkaUserResource> {
    @Autowired
    private LocalUserMapper localUserMapper;

    @Autowired
    private SocialRepository socialRepository;

    @Override
    public void save(KafkaUserResource resource) {
        socialRepository.save(GraphUser.builder().userId(resource.getId()).username(resource.getUsername()).fullName(resource.getFullName()).imageUrl(resource.getImageUrl()).build());
    }

    @Override
    public void update(KafkaUserResource resource) {
        Optional<GraphUser> optionalGraphUser = socialRepository.findById(resource.getId());
        if (optionalGraphUser.isPresent()) {
            GraphUser graphUser = optionalGraphUser.get();
            graphUser.setImageUrl(resource.getImageUrl());

            socialRepository.save(graphUser);
        }
    }

    @Override
    public void delete(KafkaUserResource resource) {
        socialRepository.deleteById(resource.getId());
    }
}