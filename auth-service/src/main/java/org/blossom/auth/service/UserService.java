package org.blossom.auth.service;

import org.blossom.auth.dto.GenericResponseDto;
import org.blossom.auth.dto.SimplifiedUserDto;
import org.blossom.auth.entity.User;
import org.blossom.auth.exception.FileDeleteException;
import org.blossom.auth.exception.FileUploadException;
import org.blossom.auth.exception.UserNotFoundException;
import org.blossom.auth.grpc.GrpcClientImageService;
import org.blossom.auth.kafka.KafkaMessageService;
import org.blossom.auth.mapper.impl.GenericDtoMapper;
import org.blossom.auth.mapper.impl.UsersDtoMapper;
import org.blossom.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private UsersDtoMapper usersDtoMapper;

    @Autowired
    private GenericDtoMapper genericDtoMapper;

    public GenericResponseDto updateUserImage(int userId, MultipartFile file)
            throws UserNotFoundException, InterruptedException, FileUploadException, FileDeleteException {
        Optional<User> optionalUser = userRepository.findByIdAndVerifiedIsTrue(userId);
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

        return genericDtoMapper.toDto("Image changed successfully", user.getId(), Map.of("url", url));
    }

    public SimplifiedUserDto getUserById(Integer userId) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findByIdAndVerifiedIsTrue(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        User user = optionalUser.get();

        return usersDtoMapper.toDto(user);
    }
}
