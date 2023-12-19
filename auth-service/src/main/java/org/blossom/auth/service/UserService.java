package org.blossom.auth.service;

import org.blossom.auth.dto.GenericResponseDto;
import org.blossom.auth.dto.SimplifiedUserDto;
import org.blossom.auth.entity.User;
import org.blossom.auth.exception.UserNotFoundException;
import org.blossom.auth.grpc.GrpcClientImageService;
import org.blossom.auth.kafka.KafkaMessageService;
import org.blossom.auth.mapper.UserMapper;
import org.blossom.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
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

    public GenericResponseDto updateUserImage(int userId, MultipartFile file)
            throws UserNotFoundException, IOException, InterruptedException {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        User user = optionalUser.get();

        if (user.getImageUrl() != null) {
            imageService.deleteImage(user.getImageUrl());
        }

        String url = imageService.uploadImage(file);

        user.setImageUrl(url);

        userRepository.save(user);

        messageService.publishUpdate(user);

        return GenericResponseDto.builder()
                .responseMessage("Image changed successfully")
                .resourceId(user.getId())
                .metadata(Map.of("url", url))
                .build();
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
