package com.zetavn.api.controller.user;

import com.zetavn.api.model.dto.UserMentionDto;
import com.zetavn.api.payload.request.FriendshipRequest;
import com.zetavn.api.payload.response.*;
import com.zetavn.api.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    public ApiResponse<Paginate<List<FriendRequestResponse>>> getRequest(@RequestParam String id,
                                                                         @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                                                                         @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize) {
        return friendshipService.friendRequestState(id, pageNumber, pageSize);
    }

    @PutMapping("/accept")
    public ApiResponse<FriendshipResponse> accept(@RequestBody FriendshipRequest friendshipRequest) {
        System.out.println("Accept Reqeust: " + friendshipRequest.toString());
        return friendshipService.accept(friendshipRequest.getSenderId(), friendshipRequest.getReceiverId());
    }

    @Transactional
    @PutMapping("/reject")
    public ApiResponse<FriendshipResponse> reject(@RequestBody FriendshipRequest friendshipRequest) {
        System.out.println("Reject Reqeust: " + friendshipRequest.toString());
        return friendshipService.rejected(friendshipRequest.getSenderId(), friendshipRequest.getReceiverId());
    }

    @GetMapping("/friends")
    public ApiResponse<Paginate<List<FriendRequestResponse>>> friends(@RequestParam String id,
                                                            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                                                            @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize) {
        return friendshipService.getFriendsByUserIdPaginate(id, pageNumber, pageSize);
    }

    @GetMapping("/suggestions")
    public ApiResponse<Paginate<List<FriendRequestResponse>>> friendSuggestions(@RequestParam String id,
                                                                      @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                                                                      @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize) {
        return friendshipService.getFriendSuggestions(id, pageNumber, pageSize);
    }

    @GetMapping("/status")
    public ApiResponse<ShortFriendshipResponse> getFriendStatus(@RequestParam(name = "sourceId") String sourceId, @RequestParam(name = "targetId") String targetId) {
        return friendshipService.getFriendshipStatus(sourceId, targetId);
    }
}
