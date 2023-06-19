package org.blossom.auth.service;

import org.blossom.auth.converter.UserConverter;
import org.blossom.auth.dto.RegisterDto;
import org.blossom.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserConverter userConverter;

    public String saveUser(RegisterDto registerDto) {
        registerDto.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        userRepository.save(userConverter.convertRegisterToUser(registerDto));
        return "User registered successfully";
    }
}
