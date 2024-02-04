package org.blossom.notification.service;

import org.blossom.notification.client.AuthClient;
import org.blossom.notification.dto.*;
import org.blossom.notification.entity.MessageNotification;
import org.blossom.notification.mapper.impl.GenericDtoMapper;
import org.blossom.notification.mapper.impl.NotificationMessagesDtoMapper;
import org.blossom.notification.repository.MessageNotificationRepository;
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
public class MessageService {
    @Autowired
    private MessageNotificationRepository messageNotificationRepository;

    @Autowired
    private AuthClient authClient;

    @Autowired
    private NotificationMessagesDtoMapper notificationMessagesDtoMapper;

    @Autowired
    private GenericDtoMapper genericDtoMapper;

    public NotificationMessagesDto getMessageNotifications(SearchParametersDto searchParameters, int userId) {
        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit(), Sort.by(Sort.Direction.DESC, "sentAt")) : Pageable.unpaged();

        Page<MessageNotification> messageNotifications = messageNotificationRepository.findByRecipientIdAndIsDeliveredFalse(userId, page);

        List<Integer> userIds = messageNotifications.get().map(MessageNotification::getSenderId).toList();

        Map<Integer, UserDto> users = userIds.stream().distinct().map(id -> authClient.getUser(id).getBody())
                .collect(Collectors.toMap(userDto -> userDto != null ? userDto.getUserId() : 0, user -> user));

        PaginationInfoDto paginationInfo = new PaginationInfoDto(messageNotifications.getTotalPages(), searchParameters.getPage(), messageNotifications.getTotalElements(), !messageNotifications.hasNext());
        return notificationMessagesDtoMapper.toDto(messageNotifications.toList(), users, userId, paginationInfo);
    }

    public GenericResponseDto confirmUserReceivedNotification(String notificationId, int userId) {
        Optional<MessageNotification> optionalNotification = messageNotificationRepository.findById(notificationId);
        if (optionalNotification.isEmpty()) {
            return null;
        }

        MessageNotification notification = optionalNotification.get();

        if (notification.getRecipientId() != userId) {
            return null;
        }

        notification.setDelivered(true);
        messageNotificationRepository.save(notification);

        return genericDtoMapper.toDto("Notification updated successfully", notificationId, null);
    }
}
