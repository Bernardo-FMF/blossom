package org.blossom.auth.service;

import org.blossom.auth.delta.DeltaEngine;
import org.blossom.auth.delta.markable.UserMarkable;
import org.blossom.auth.dto.SimplifiedUserDto;
import org.blossom.auth.entity.User;
import org.blossom.auth.exception.UserNotFoundException;
import org.blossom.auth.grpc.GrpcClientImageService;
import org.blossom.auth.kafka.KafkaMessageService;
import org.blossom.auth.mapper.UserMapper;
import org.blossom.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GrpcClientImageService imageService;

    @Autowired
    private KafkaMessageService messageService;

    @Autowired
    private UserMapper userMapper;

    public String updateUserImage(int userId, int loggedUserId, MultipartFile file)
            throws UserNotFoundException, IOException, InterruptedException, BadCredentialsException {
        if (userId != loggedUserId) {
            throw new BadCredentialsException("Logged in user does not have permission to change other users profile image");
        }

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        User user = optionalUser.get();

        if (user.getImageUrl() != null) {
            imageService.deleteImage(user.getImageUrl());
        }

        String url = imageService.uploadImage(file);

        UserMarkable userMarkable = new UserMarkable()
                .markImageUrl(url);

        DeltaEngine<UserMarkable, User> deltaEngine = new DeltaEngine<>((markable, entity) -> {
            if (markable.isMarkedImageUrl()) {
                entity.setImageUrl(markable.getDelegate().getImageUrl());
            }
        });

        deltaEngine.applyDelta(userMarkable, user);

        userRepository.save(user);

        messageService.publishUpdate(user);

        return url;
    }

    public SimplifiedUserDto getUserById(Integer userId) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        User user = optionalUser.get();

        return userMapper.mapToSimplifiedUser(user);
    }
}
