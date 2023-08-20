package org.blossom.kafka.inbound.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class LocalUser implements Serializable {
    private int id;
    private String fullName;
    private String username;
    private String imageUrl;
}