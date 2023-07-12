package org.blossom.kafka;

import org.blossom.facade.KafkaResourceHandler;
import org.blossom.kafka.converter.LocalUserConverter;
import org.blossom.model.KafkaUserResource;
import org.blossom.repository.LocalUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserResourceHandler implements KafkaResourceHandler<KafkaUserResource> {
    @Autowired
    private LocalUserRepository localUserRepository;

    @Autowired
    private LocalUserConverter converter;

    @Override
    public void save(KafkaUserResource resource) {
        localUserRepository.save(Objects.requireNonNull(converter.convert(resource)));
    }

    @Override
    public void update(KafkaUserResource resource) {
        localUserRepository.save(Objects.requireNonNull(converter.convert(resource)));
    }

    @Override
    public void delete(KafkaUserResource resource) {
        localUserRepository.deleteById(resource.getId());
    }
}
