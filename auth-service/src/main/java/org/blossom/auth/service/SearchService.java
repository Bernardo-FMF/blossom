package org.blossom.auth.service;

import org.blossom.auth.dto.SearchParametersDto;
import org.blossom.auth.dto.SimplifiedUserDto;
import org.blossom.auth.dto.UsersDto;
import org.blossom.auth.entity.User;
import org.blossom.auth.mapper.UserMapper;
import org.blossom.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SearchService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public UsersDto userLookup(SearchParametersDto searchParameters) {
        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit()) : null;
        Page<User> localUsers = userRepository.findByUsernameContainingIgnoreCaseOrFullNameContainingIgnoreCase(searchParameters.getContains(), searchParameters.getContains(), page);

        return UsersDto.builder()
                .users(localUsers.get().map(user -> userMapper.mapToSimplifiedUser(user)).collect(Collectors.toSet()))
                .totalPages(localUsers.getTotalPages())
                .currentPage(searchParameters.getPage())
                .totalElements(localUsers.getTotalElements())
                .eof(!localUsers.hasNext())
                .build();
    }

    public SimplifiedUserDto userLookupByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        User user = optionalUser.get();
        return userMapper.mapToSimplifiedUser(user);
    }
}
