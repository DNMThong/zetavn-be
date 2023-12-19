package com.zetavn.api.service.impl;

import com.zetavn.api.enums.MessageStatusEnum;
import com.zetavn.api.enums.MessageTypeEnum;
import com.zetavn.api.exception.InvalidFieldException;
import com.zetavn.api.exception.NotFoundException;
import com.zetavn.api.model.entity.MessageCallEntity;
import com.zetavn.api.model.entity.MessageEntity;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.model.mapper.CommentMapper;
import com.zetavn.api.model.mapper.MessageMapper;
import com.zetavn.api.payload.request.MessageCallRequest;
import com.zetavn.api.payload.request.MessageRequest;
import com.zetavn.api.payload.response.*;
import com.zetavn.api.repository.ActivityLogRepository;
import com.zetavn.api.repository.MesageCallRepository;
import com.zetavn.api.repository.MessageRepository;
import com.zetavn.api.repository.UserRepository;
import com.zetavn.api.service.CloudinaryService;
import com.zetavn.api.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private MesageCallRepository mesageCallRepository;

    @Override
    public MessageResponse createMessage(MessageRequest message,String senderId) {
        UserEntity userReciever =  userRepository.findById(message.getRecieverId()).orElseThrow(() -> {throw new NotFoundException("Not found user reciever by id");});
        UserEntity userSender =  userRepository.findById(senderId).orElseThrow(() -> {throw new NotFoundException("Not found user sender by id");});

        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setCreatedAt(LocalDateTime.now());
        messageEntity.setMessage(message.getMessage());
        messageEntity.setSenderUser(userSender);
        messageEntity.setRecieverUser(userReciever);
        messageEntity.setStatus(MessageStatusEnum.SENT);
        messageEntity.setType(message.getType());
        System.out.println("MESSAGE "+LocalDateTime.now().toString());

        MessageEntity messageResponse = messageRepository.save(messageEntity);
        System.out.println("messageResponse "+messageResponse.getCreatedAt().toString());

        if(message.getType().equals(MessageTypeEnum.CALL)) {
            MessageCallRequest callRequest = message.getCall();
            if(callRequest==null) throw new InvalidFieldException("Field call is null");

            MessageCallEntity messageCall = new MessageCallEntity();
            messageCall.setDuration(callRequest.getDuration());
            messageCall.setStatus(callRequest.getStatus());
            messageCall.setType(callRequest.getType());
            messageCall.setCreatedAt(LocalDateTime.now());
            messageCall.setMessage(messageResponse);

            MessageCallEntity messageCallResponse = mesageCallRepository.save(messageCall);
            messageResponse.setMessageCall(messageCallResponse);
        }

        return MessageMapper.entityToDto(messageResponse) ;
    }

    @Override
    public MessageResponse createMessageFile(MessageRequest message, MultipartFile file,String senderId) {
        FileUploadResponse fileUploadResponse = null;
        if(message.getType().equals(MessageTypeEnum.IMAGE)) {
            fileUploadResponse = cloudinaryService.upload(file,"images/","image");
        }else if(message.getType().equals(MessageTypeEnum.VIDEO)) {
            fileUploadResponse = cloudinaryService.upload(file,"videos/","video");
        }
        if(fileUploadResponse!=null) {
            message.setMessage(fileUploadResponse.getUrl());
            return this.createMessage(message,senderId);
        }
        return null;
    }

    @Override
    public MessageResponse updateStatusMessage(Long id, MessageStatusEnum status) {
        MessageEntity message = messageRepository.findById(id).orElseThrow(() -> {throw new NotFoundException("Not found message by id "+id);});
        message.setStatus(status);
        MessageEntity messageResponse = messageRepository.save(message);
        return MessageMapper.entityToDto(messageResponse) ;
    }

    @Override
    public Paginate<List<MessageResponse>> getChatMessage(String userIdGetChat, String userIdContact,Integer pageNumber, Integer pageSize) {
        UserEntity userGetChat =  userRepository.findById(userIdGetChat).orElseThrow(() -> {throw new NotFoundException("Not found user user by id "+userIdGetChat);});
        UserEntity userContact =  userRepository.findById(userIdContact).orElseThrow(() -> {throw new NotFoundException("Not found user user by id "+userIdContact);});

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<MessageEntity> messagePage = messageRepository.getChatMessagePagination(userIdGetChat,userIdContact,pageable);
        List<MessageEntity> messages = messagePage.getContent();
        List<MessageEntity> messagesUpadteRead = new ArrayList<>();

        for (MessageEntity message : messages) {
            if (message.getStatus().equals(MessageStatusEnum.SENT)
                    && message.getRecieverUser().getUserId().equals(userIdGetChat)) {
                message.setStatus(MessageStatusEnum.READ);

                MessageEntity messageUpdate = messageRepository.save(message);
                MessageResponse messageResponse = MessageMapper.entityToDto(messageUpdate);

                template.convertAndSendToUser(messageResponse.getSender().getId(),"/topic/message/update-read",messageResponse);
            }
            messagesUpadteRead.add(message);
        }

        List<MessageResponse> messageResponses = messagesUpadteRead.stream().map(MessageMapper::entityToDto).collect(Collectors.toList());

        Paginate<List<MessageResponse>> dataResponse = new Paginate<>(
                messagePage.getNumber(),
                messagePage.getSize(),
                messagePage.getTotalElements(),
                messagePage.getTotalPages(),
                messagePage.isLast(),
                messageResponses
        );
        return dataResponse;
    }
}
