package org.blossom.social.kafka.inbound;

import org.blossom.facade.KafkaResourceHandler;
import org.blossom.model.KafkaUserResource;
import org.blossom.social.entity.GraphUser;
import org.blossom.social.factory.impl.GraphUserFactory;
import org.blossom.social.mapper.impl.UserDtoMapper;
import org.blossom.social.repository.SocialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserResourceHandler implements KafkaResourceHandler<KafkaUserResource> {
    @Autowired
    private UserDtoMapper userDtoMapper;

    @Autowired
    private SocialRepository socialRepository;

    @Autowired
    private GraphUserFactory graphUserFactory;

    @Override
    public void save(KafkaUserResource resource) {
        socialRepository.save(graphUserFactory.buildEntity(resource));
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