package org.blossom.repository;

import org.blossom.localmodel.LocalUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface LocalUserRepository extends ElasticsearchRepository<LocalUser, Integer> {
    @Query("{\"bool\": {\"should\": [{\"wildcard\": {\"username\": \"*?0*\"}}, {\"wildcard\": {\"fullName\": \"*?0*\"}}]}}")
    Page<LocalUser> findByNameSimilar(String searchTerm, Pageable pageable);
}