package org.blossom.notification.service;

import org.blossom.notification.client.AuthClient;
import org.blossom.notification.dto.NotificationMessageDto;
import org.blossom.notification.dto.NotificationMessagesDto;
import org.blossom.notification.dto.SearchParametersDto;
import org.blossom.notification.dto.UserDto;
import org.blossom.notification.entity.MessageNotification;
import org.blossom.notification.mapper.NotificationMessageDtoMapper;
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
    private NotificationMessageDtoMapper notificationMessageDtoMapper;

    public NotificationMessagesDto getMessageNotifications(SearchParametersDto searchParameters, int userId) {
        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit(), Sort.by(Sort.Direction.DESC, "sentAt")) : Pageable.unpaged();

        Page<MessageNotification> messageNotifications = messageNotificationRepository.findByRecipientIdAndIsDeliveredFalse(userId, page);

        return getNotificationMessagesDto(messageNotifications);
    }

    public String confirmUserReceivedNotification(String notificationId, int userId) {
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

        return "Notification updated successfully";
    }

    private NotificationMessagesDto getNotificationMessagesDto(Page<MessageNotification> messageNotifications) {
        List<Integer> userIds = messageNotifications.get().map(MessageNotification::getSenderId).toList();

        Map<Integer, UserDto> users = userIds.stream().map(id -> authClient.getUser(id).getBody())
                .collect(Collectors.toMap(userDto -> userDto != null ? userDto.getUserId() : 0, user -> user));

        List<NotificationMessageDto> messages = messageNotifications.get().map(message -> notificationMessageDtoMapper.mapToNotificationMessageDto(message, users.get(message.getSenderId()))).toList();

        return NotificationMessagesDto.builder()
                .notificationMessages(messages)
                .currentPage(messageNotifications.getNumber())
                .totalPages(messageNotifications.getTotalPages())
                .totalElements(messageNotifications.getTotalElements())
                .eof(!messageNotifications.hasNext())
                .build();
    }
}
