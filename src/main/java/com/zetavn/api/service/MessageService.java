package com.zetavn.api.service;

import com.zetavn.api.enums.MessageStatusEnum;
import com.zetavn.api.payload.request.MessageRequest;
import com.zetavn.api.payload.response.MessageResponse;
import com.zetavn.api.payload.response.Paginate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MessageService {
    MessageResponse createMessage(MessageRequest message,String senderId);
    MessageResponse createMessageFile(MessageRequest message, MultipartFile file,String senderId);

    MessageResponse updateStatusMessage(Long id, MessageStatusEnum status);
    Paginate<List<MessageResponse>> getChatMessage(String userIdGetChat,String userIdContact,Integer pageNumber, Integer pageSize);
}
