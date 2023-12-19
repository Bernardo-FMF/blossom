package org.blossom.auth.service;

import org.blossom.auth.dto.PaginationInfoDto;
import org.blossom.auth.dto.SearchParametersDto;
import org.blossom.auth.dto.SimplifiedUserDto;
import org.blossom.auth.dto.UsersDto;
import org.blossom.auth.entity.User;
import org.blossom.auth.mapper.impl.UsersDtoMapper;
import org.blossom.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SearchService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UsersDtoMapper usersDtoMapper;

    public UsersDto userLookup(SearchParametersDto searchParameters) {
        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit()) : null;
        Page<User> localUsers = userRepository.findByUsernameContainingIgnoreCaseOrFullNameContainingIgnoreCase(searchParameters.getContains(), searchParameters.getContains(), page);

        return usersDtoMapper.toPaginatedDto(localUsers.getContent(), PaginationInfoDto.builder()
                .totalPages(localUsers.getTotalPages())
                .currentPage(searchParameters.getPage())
                .totalElements(localUsers.getTotalElements())
                .eof(!localUsers.hasNext())
                .build());
    }

    public SimplifiedUserDto userLookupByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        return usersDtoMapper.toDto(optionalUser.get());
    }
}
