package org.blossom.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KafkaUserResource extends KafkaResource {
    int id;
    String fullName;
    String username;
    String imageUrl;

    @Override
    public String toString() {
        return "KafkaUserResource(id=" + this.getId() +
                ", fullName=" + this.getFullName() +
                ", username=" + this.getUsername() +
                ", imageUrl=" + this.getImageUrl() + ")";
    }
}
