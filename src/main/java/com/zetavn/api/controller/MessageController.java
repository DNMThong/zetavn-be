package com.zetavn.api.controller;


import com.zetavn.api.enums.MessageStatusEnum;
import com.zetavn.api.payload.request.MessageRequest;
import com.zetavn.api.payload.response.ApiResponse;
import com.zetavn.api.payload.response.CommentResponse;
import com.zetavn.api.payload.response.MessageResponse;
import com.zetavn.api.payload.response.Paginate;
import com.zetavn.api.service.MessageService;
import com.zetavn.api.service.ZegocloudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v0/messages")
public class MessageController {
    private final SimpMessagingTemplate template;

    private final MessageService messageService;

    private final ZegocloudService zegocloudService;

    @Autowired
    public MessageController(SimpMessagingTemplate template, MessageService messageService,ZegocloudService zegocloudService) {
        this.template = template;
        this.messageService = messageService;
        this.zegocloudService = zegocloudService;
    }

    @GetMapping("/token-call")
    public ApiResponse<Map<String, String>> getTokenCall() {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String token = zegocloudService.generateToken(userId);

        Map<String, String> rs = new HashMap<>();
        rs.put("token",token);
        rs.put("userId",userId);

        return ApiResponse.success(HttpStatus.OK,"Get token call success",rs);
    }

    @GetMapping("")
    public ApiResponse<Paginate<List<MessageResponse>>> getChatMessages(
                                        @RequestParam(value = "userId", required = true)   String userIdContact,
                                        @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                                        @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize ) {
        String userIdGetChat = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Paginate<List<MessageResponse>> messages = messageService.getChatMessage(userIdGetChat,userIdContact,pageNumber,pageSize);

        return ApiResponse.success(HttpStatus.OK,"Get chat message success",messages);
    }

    @PostMapping("")
    public ApiResponse<MessageResponse> createMessage(
            @RequestBody MessageRequest messageRequest
          ) {
        String senderId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MessageResponse message = messageService.createMessage(messageRequest,senderId);

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
        String senderId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MessageResponse message = messageService.createMessageFile(messageRequest,file,senderId);

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
