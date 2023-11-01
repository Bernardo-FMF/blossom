package org.blossom.notification.service;

import org.blossom.notification.client.AuthClient;
import org.blossom.notification.dto.NotificationFollowDto;
import org.blossom.notification.dto.NotificationFollowsDto;
import org.blossom.notification.dto.SearchParametersDto;
import org.blossom.notification.dto.UserDto;
import org.blossom.notification.entity.FollowNotification;
import org.blossom.notification.mapper.NotificationFollowDtoMapper;
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
    private NotificationFollowDtoMapper notificationFollowDtoMapper;

    public NotificationFollowsDto getFollowNotifications(SearchParametersDto searchParameters, int userId) {
        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit(), Sort.by(Sort.Direction.DESC, "followedAt")) : Pageable.unpaged();

        Page<FollowNotification> followNotifications = followNotificationRepository.findByRecipientIdAndIsDeliveredFalse(userId, page);

        return getNotificationFollowsDto(followNotifications);
    }

    public String confirmUserReceivedNotification(String notificationId, int userId) {
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

        return "Notification updated successfully";
    }

    private NotificationFollowsDto getNotificationFollowsDto(Page<FollowNotification> followNotifications) {
        List<Integer> userIds = followNotifications.get().map(FollowNotification::getSenderId).toList();

        Map<Integer, UserDto> users = userIds.stream().map(id -> authClient.getUser(id).getBody())
                .collect(Collectors.toMap(userDto -> userDto != null ? userDto.getUserId() : 0, user -> user));

        List<NotificationFollowDto> follows = followNotifications.get().map(message -> notificationFollowDtoMapper.mapToNotificationFollowDto(message, users.get(message.getSenderId()))).toList();

        return NotificationFollowsDto.builder()
                .notificationFollows(follows)
                .currentPage(followNotifications.getNumber())
                .totalPages(followNotifications.getTotalPages())
                .totalElements(followNotifications.getTotalElements())
                .eof(!followNotifications.hasNext())
                .build();
    }
}
