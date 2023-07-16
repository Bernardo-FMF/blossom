package org.blossom.service;

import com.google.common.collect.ImmutableSet;
import org.blossom.dto.LocalUsersDto;
import org.blossom.dto.SearchParametersDto;
import org.blossom.localmodel.LocalUser;
import org.blossom.repository.LocalUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SearchService {
    @Autowired
    private LocalUserRepository localUserRepository;

    public LocalUsersDto userLookup(SearchParametersDto searchParametersDto) {
        Pageable page = buildPage(searchParametersDto);
        Page<LocalUser> localUsers = localUserRepository.findByUsernameSimilar(searchParametersDto.getContains(), page);

        return LocalUsersDto.builder()
                .localUsers(ImmutableSet.copyOf(localUsers.getContent()))
                .eof(!localUsers.hasNext())
                .build();
    }

    private Pageable buildPage(SearchParametersDto searchParametersDto) {
        return searchParametersDto.hasPagination() ? PageRequest.of(searchParametersDto.getPage(), searchParametersDto.getPageLimit()) : null;
    }
}
