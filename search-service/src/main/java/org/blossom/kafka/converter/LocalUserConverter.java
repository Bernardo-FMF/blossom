package org.blossom.kafka.converter;

import org.blossom.localmodel.LocalUser;
import org.blossom.model.KafkaUserResource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class LocalUserConverter implements Converter<KafkaUserResource, LocalUser> {
    @Override
    public LocalUser convert(KafkaUserResource source) {
        return LocalUser.builder()
                .id(source.getId())
                .fullName(source.getFullName())
                .userName(source.getUserName())
                .imageUrl(source.getImageUrl())
                .build();
    }
}
