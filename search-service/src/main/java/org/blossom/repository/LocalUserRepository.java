package org.blossom.repository;

import org.blossom.localmodel.LocalUser;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface LocalUserRepository extends ElasticsearchRepository<LocalUser, Integer> {
}