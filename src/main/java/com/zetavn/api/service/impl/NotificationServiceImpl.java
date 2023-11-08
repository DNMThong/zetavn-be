package com.zetavn.api.service.impl;

import com.zetavn.api.enums.PostNotificationEnum;
import com.zetavn.api.exception.NotFoundException;
import com.zetavn.api.model.entity.PostEntity;
import com.zetavn.api.model.entity.PostNotificationEntity;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.model.mapper.PostNotificationMapper;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.CommentResponse;
import com.zetavn.api.payload.response.Paginate;
import com.zetavn.api.payload.response.PostNotificationResponse;
import com.zetavn.api.repository.NotificationRepository;
import com.zetavn.api.repository.PostRepository;
import com.zetavn.api.repository.UserRepository;
import com.zetavn.api.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PostRepository postRepository;


    @Override
    public PostNotificationResponse createNotification(String interacting, String receiving, String postId, PostNotificationEnum type, long relatedId) {
        UserEntity interact = userRepository.findById(interacting)
                .orElseThrow(() -> new NotFoundException("Interacting user not found"));
        UserEntity receiver = userRepository.findById(receiving)
                .orElseThrow(() -> new NotFoundException("Receiving user not found"));

        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        PostNotificationEntity postNotificationEntity = new PostNotificationEntity();
        postNotificationEntity.setInteractingUser(interact);
        postNotificationEntity.setReceivingUser(receiver);
        postNotificationEntity.setCreatedAt(LocalDateTime.now());
        postNotificationEntity.setType(type);
        postNotificationEntity.setRead(false);
        postNotificationEntity.setPost(post);
        postNotificationEntity.setRelatedId(relatedId);
        notificationRepository.save(postNotificationEntity);

        return PostNotificationMapper.entityToResponse(postNotificationEntity);
    }

    @Override
    public Paginate<List<PostNotificationResponse>> listNotification(String userId, Integer pageNumber, Integer pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<PostNotificationEntity> list = notificationRepository.getListNotification(userId, pageable);
            List<PostNotificationEntity> listEntity = list.getContent();
            List<PostNotificationResponse> responseList = listEntity.stream().map(PostNotificationMapper::entityToResponse).toList();
            return new Paginate<>(
                    list.getNumber(),
                    list.getSize(),
                    list.getTotalElements(),
                    list.getTotalPages(),
                    list.isLast(),
                    responseList
            );
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    @Override
    public void deleteNotification(Long relatedId, PostNotificationEnum type) {
        notificationRepository.deletePostNotification(relatedId, type);
    }
}
