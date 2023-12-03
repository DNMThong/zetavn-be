package com.zetavn.api.controller.user;

import com.zetavn.api.model.dto.UserMentionDto;
import com.zetavn.api.payload.request.FriendshipRequest;
import com.zetavn.api.payload.response.*;
import com.zetavn.api.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v0/friendship")
public class FriendshipController {
    private final FriendshipService friendshipService;

    FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }


    @PostMapping("")
    public ApiResponse<FriendshipResponse> sendRequest(@RequestBody FriendshipRequest friendshipRequest) {
        return friendshipService.sendRequest(friendshipRequest);
    }

    @GetMapping("")
    public ApiResponse<Paginate<List<FriendRequestResponse>>> getRequest(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                                                                         @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize) {
        try{
            String id = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return friendshipService.friendRequestState(id, pageNumber, pageSize);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/accept")
    public ApiResponse<FriendshipResponse> accept(@RequestBody FriendshipRequest friendshipRequest) {
        try{
            System.out.println("Accept Reqeust: " + friendshipRequest.toString());
            String id = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return friendshipService.accept(friendshipRequest.getUserId(), id);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @Transactional
    @PutMapping("/reject")
    public ApiResponse<FriendshipResponse> reject(@RequestBody FriendshipRequest friendshipRequest) {
        try {
            System.out.println("Reject Reqeust: " + friendshipRequest.toString());
            String id = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return friendshipService.accept(friendshipRequest.getUserId(), id);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/friends")
    public ApiResponse<Paginate<List<FriendRequestResponse>>> friends(@RequestParam String id,
                                                            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                                                            @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize) {
        return friendshipService.getFriendsByUserIdPaginate(id, pageNumber, pageSize);
    }

    @GetMapping("/suggestions")
    public ApiResponse<Paginate<List<FriendRequestResponse>>> friendSuggestions(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                                                                                @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize) {
        try {
            String id = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return friendshipService.getFriendSuggestions(id, pageNumber, pageSize);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/status")
    public ApiResponse<ShortFriendshipResponse> getFriendStatus(@RequestParam(name = "targetId") String targetId) {
        try {
            String sourceId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return friendshipService.getFriendshipStatus(sourceId, targetId);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
