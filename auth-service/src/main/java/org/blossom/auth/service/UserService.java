package org.blossom.auth.service;

import org.blossom.auth.converter.UserConverter;
import org.blossom.auth.dto.RegisterDto;
import org.blossom.auth.dto.TokenDto;
import org.blossom.auth.entity.Role;
import org.blossom.auth.entity.User;
import org.blossom.auth.enums.RoleEnum;
import org.blossom.auth.exception.EmailInUseException;
import org.blossom.auth.exception.NoRoleFoundException;
import org.blossom.auth.exception.UsernameInUseException;
import org.blossom.auth.repository.RoleRepository;
import org.blossom.auth.repository.UserRepository;
import org.blossom.auth.security.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Autowired
    private RoleRepository roleRepository;

    public String saveUser(RegisterDto registerDto) throws UsernameInUseException, EmailInUseException, NoRoleFoundException {
        registerDto.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new UsernameInUseException("Username is already in use");
        }

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new EmailInUseException("Email is already in use");
        }

        Optional<Role> role = roleRepository.findByName(RoleEnum.USER);

        if (role.isEmpty()) {
            throw new NoRoleFoundException("User role is not present");
        }

        userRepository.save(Objects.requireNonNull(userConverter.convert(registerDto, role.get())));
        return "User registered successfully";
    }

    public String generateToken(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return tokenGenerator.generateToken(user.get());
    }

    public TokenDto validateToken(String token) {
        return tokenGenerator.validateToken(token);
    }
}
