package com.zetavn.api.model.mapper;

import com.zetavn.api.model.entity.PostNotificationEntity;
import com.zetavn.api.payload.response.PostNotificationResponse;


public class PostNotificationMapper {
    public static PostNotificationResponse entityToResponse(PostNotificationEntity postNotificationEntity) {
        PostNotificationResponse notification = new PostNotificationResponse();
        notification.setId(postNotificationEntity.getId());
        notification.setPostId(postNotificationEntity.getPost().getPostId());
        notification.setInteracting(OverallUserMapper.entityToDto(postNotificationEntity.getInteractingUser()));
        notification.setReceiving(OverallUserMapper.entityToDto(postNotificationEntity.getReceivingUser()));
        notification.setCreatedAt(postNotificationEntity.getCreatedAt());
        notification.setType(postNotificationEntity.getType());
        notification.setIsRead(postNotificationEntity.isRead());
        notification.setRelatedId(postNotificationEntity.getRelatedId());
        notification.setIsCancel(false);
        return notification;
    }
}
