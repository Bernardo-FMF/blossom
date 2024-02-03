package org.blossom.social.kafka.inbound;

import lombok.extern.log4j.Log4j2;
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
@Log4j2
public class UserResourceHandler implements KafkaResourceHandler<KafkaUserResource> {
    @Autowired
    private UserDtoMapper userDtoMapper;

    @Autowired
    private SocialRepository socialRepository;

    @Autowired
    private GraphUserFactory graphUserFactory;

    @Override
    public void save(KafkaUserResource resource) {
        log.info("processing save message of type user: {}", resource);

        socialRepository.save(graphUserFactory.buildEntity(resource));
    }

    @Override
    public void update(KafkaUserResource resource) {
        log.info("processing update message of type user: {}", resource);

        Optional<GraphUser> optionalGraphUser = socialRepository.findById(resource.getId());
        if (optionalGraphUser.isPresent()) {
            GraphUser graphUser = optionalGraphUser.get();
            graphUser.setImageUrl(resource.getImageUrl());

            socialRepository.save(graphUser);
        }
    }

    @Override
    public void delete(KafkaUserResource resource) {
        log.info("processing delete message of type user: {}", resource);

        socialRepository.deleteById(resource.getId());
    }
}