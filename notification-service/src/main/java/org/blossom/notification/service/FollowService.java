package org.blossom.notification.service;

import org.blossom.notification.client.AuthClient;
import org.blossom.notification.dto.*;
import org.blossom.notification.entity.FollowNotification;
import org.blossom.notification.mapper.impl.GenericDtoMapper;
import org.blossom.notification.mapper.impl.NotificationFollowsDtoMapper;
import org.blossom.notification.repository.FollowNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FollowService {
    @Autowired
    private FollowNotificationRepository followNotificationRepository;

    @Autowired
    private AuthClient authClient;

    @Autowired
    private NotificationFollowsDtoMapper notificationFollowsDtoMapper;

    @Autowired
    private GenericDtoMapper genericDtoMapper;

    public NotificationFollowsDto getFollowNotifications(SearchParametersDto searchParameters, int userId) {
        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit(), Sort.by(Sort.Direction.DESC, "followedAt")) : Pageable.unpaged();

        Page<FollowNotification> followNotifications = followNotificationRepository.findByRecipientIdAndIsDeliveredFalse(userId, page);

        List<Integer> userIds = followNotifications.get().map(FollowNotification::getSenderId).toList();

        Map<Integer, UserDto> users = userIds.stream().map(id -> authClient.getUser(id).getBody())
                .collect(Collectors.toMap(userDto -> userDto != null ? userDto.getUserId() : 0, user -> user));

        PaginationInfoDto paginationInfo = new PaginationInfoDto(followNotifications.getTotalPages(), searchParameters.getPage(), followNotifications.getTotalElements(), !followNotifications.hasNext());
        return notificationFollowsDtoMapper.toDto(followNotifications.getContent(), users, paginationInfo);
    }

    public GenericResponseDto confirmUserReceivedNotification(String notificationId, int userId) {
        Optional<FollowNotification> optionalNotification = followNotificationRepository.findById(notificationId);
        if (optionalNotification.isEmpty()) {
            return null;
        }

        FollowNotification notification = optionalNotification.get();

        if (notification.getRecipientId() != userId) {
            return null;
        }

        notification.setDelivered(true);
        followNotificationRepository.save(notification);

        return genericDtoMapper.toDto("Notification updated successfully", notificationId, null);
    }
}
