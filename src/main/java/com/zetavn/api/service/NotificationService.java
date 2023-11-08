package com.zetavn.api.service;

import com.zetavn.api.enums.PostNotificationEnum;
import com.zetavn.api.payload.response.Paginate;
import com.zetavn.api.payload.response.PostNotificationResponse;

import java.util.List;

public interface NotificationService {
    PostNotificationResponse createNotification(String interacting, String receiving, String postId,
                                                PostNotificationEnum type, long relatedId);

    Paginate<List<PostNotificationResponse>> listNotification(String userId, Integer pageNumber, Integer pageSize);

    void deleteNotification(Long relatedId, PostNotificationEnum type);
}
