package org.blossom.repository;

import org.blossom.localmodel.LocalUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface LocalUserRepository extends ElasticsearchRepository<LocalUser, Integer> {
    @Query("{\"wildcard\": {\"username\": \"*?0*\"}}")
    Page<LocalUser> findByUsernameSimilar(String searchTerm, Pageable pageable);
}