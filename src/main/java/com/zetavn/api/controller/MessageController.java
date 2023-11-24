package com.zetavn.api.controller;


import com.zetavn.api.enums.MessageStatusEnum;
import com.zetavn.api.payload.request.MessageRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.CommentResponse;
import com.zetavn.api.payload.response.MessageResponse;
import com.zetavn.api.payload.response.Paginate;
import com.zetavn.api.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v0/messages")
public class MessageController {
    private final SimpMessagingTemplate template;

    private final MessageService messageService;

    @Autowired
    public MessageController(SimpMessagingTemplate template, MessageService messageService) {
        this.template = template;
        this.messageService = messageService;
    }

    @GetMapping("")
    public ApiResponse<Paginate<List<MessageResponse>>> getChatMessages(
                                        @RequestParam(value = "userIdGetChat", required = true)  String userIdGetChat,
                                        @RequestParam(value = "userIdContact", required = true)   String userIdContact,
                                        @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                                        @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize ) {
        Paginate<List<MessageResponse>> messages = messageService.getChatMessage(userIdGetChat,userIdContact,pageNumber,pageSize);

        return ApiResponse.success(HttpStatus.OK,"Get chat message success",messages);
    }

    @PostMapping("")
    public ApiResponse<MessageResponse> createMessage(
            @RequestBody MessageRequest messageRequest
          ) {
        MessageResponse message = messageService.createMessage(messageRequest);

        if(message!=null) {
            template.convertAndSendToUser(message.getReciever().getId(),"/topic/message",message);
        }
        return ApiResponse.success(HttpStatus.CREATED,"Get chat message success",message);
    }

    @PostMapping("/file")
    public ApiResponse<MessageResponse> createMessageFile(
            @ModelAttribute MessageRequest messageRequest,
            @RequestPart("file") MultipartFile file
    ) {
        MessageResponse message = messageService.createMessageFile(messageRequest,file);

        if(message!=null) {
            template.convertAndSendToUser(message.getReciever().getId(),"/topic/message",message);
        }
        return ApiResponse.success(HttpStatus.CREATED,"Get chat message success",message);
    }

    @PutMapping("/{id}")
    public ApiResponse<MessageResponse> updateReadMessage(
            @PathVariable("id") Long id
    ) {
        MessageResponse message = messageService.updateStatusMessage(id, MessageStatusEnum.READ);
        if(message!=null) {
            template.convertAndSendToUser(message.getSender().getId(),"/topic/message/update-read",message);
        }
        return ApiResponse.success(HttpStatus.CREATED,"Get chat message success",message);
    }

}
