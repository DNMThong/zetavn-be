package com.zetavn.api.service.impl;

import com.cloudinary.Api;
import com.zetavn.api.enums.FriendStatusEnum;
import com.zetavn.api.enums.NotiFriendRequestEnum;
import com.zetavn.api.enums.StatusFriendsEnum;
import com.zetavn.api.exception.DuplicateRecordException;
import com.zetavn.api.exception.NotFoundException;
import com.zetavn.api.model.entity.FriendshipEntity;
import com.zetavn.api.model.entity.PostEntity;
import com.zetavn.api.model.entity.UserEntity;
import com.zetavn.api.model.mapper.FriendshipMapper;
import com.zetavn.api.model.mapper.OverallUserMapper;
import com.zetavn.api.model.mapper.UserMapper;
import com.zetavn.api.model.mapper.UserSearchMapper;
import com.zetavn.api.payload.request.FollowRequest;
import com.zetavn.api.payload.request.FriendshipRequest;
import com.zetavn.api.payload.response.*;
import com.zetavn.api.repository.ActivityLogRepository;
import com.zetavn.api.repository.FriendshipRepository;
import com.zetavn.api.repository.UserRepository;
import com.zetavn.api.service.FollowService;
import com.zetavn.api.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FriendshipServiceImpl implements FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final FriendshipMapper friendshipMapper;
    private final FollowService followService;
    private final UserRepository userRepository;

    private final SimpMessagingTemplate simpMessagingTemplate;


    @Autowired
    FriendshipServiceImpl(FriendshipRepository friendshipRepository,
                          FriendshipMapper friendshipMapper,
                          FollowService followService,
                          UserRepository userRepository,
                          SimpMessagingTemplate simpMessagingTemplate) {
        this.friendshipRepository = friendshipRepository;
        this.friendshipMapper = friendshipMapper;
        this.followService = followService;
        this.userRepository = userRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public ApiResponse<FriendshipResponse> sendRequest(FriendshipRequest friendshipRequest) {
        try {
            String id = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            FriendshipEntity existingFriendship = friendshipRepository.getFriendshipByUserID(id, friendshipRequest.getUserId());
            if(existingFriendship != null && existingFriendship.getStatus() == FriendStatusEnum.ACCEPTED) throw new DuplicateRecordException("This relationship already exists");

            if (existingFriendship != null) {
                if (existingFriendship.getStatus() == FriendStatusEnum.PENDING) {
                    return ApiResponse.error(HttpStatus.BAD_REQUEST, "Friend request already exists", null);
                } else if (existingFriendship.getStatus() == FriendStatusEnum.REJECTED) {
                    friendshipRepository.deleteById(existingFriendship.getFriendshipId());
                }
            }

            FriendshipEntity friendship = new FriendshipEntity();
            friendship.setSenderUserEntity(userRepository.findById(id).get());
            friendship.setReceiverUserEntity(userRepository.findById(friendshipRequest.getUserId()).get());
            friendship.setStatus(FriendStatusEnum.PENDING);
            friendship.setCreatedAt(LocalDateTime.now());

            FollowRequest follow = new FollowRequest();
            follow.setFollowerId(friendship.getSenderUserEntity().getUserId());
            follow.setFollowingId(friendship.getReceiverUserEntity().getUserId());
            followService.friendshipFollow(follow);

            FriendshipEntity saveFriendship = friendshipRepository.save(friendship);

            // Send socket
            FriendRequestResponse friendRequestResponse = new FriendRequestResponse();
            friendRequestResponse.setUser(OverallUserMapper.entityToDto(saveFriendship.getSenderUserEntity()));
            friendRequestResponse.setCreatedAt(saveFriendship.getCreatedAt());
            friendRequestResponse.setStatus(NotiFriendRequestEnum.PENDING);

            simpMessagingTemplate.convertAndSendToUser(saveFriendship.getReceiverUserEntity().getUserId(),"/topic/friendship",friendRequestResponse);


            return ApiResponse.success(HttpStatus.OK, "send request success!", friendshipMapper.entityToFriendshipResponse(saveFriendship));
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @Override
    public ApiResponse<Paginate<List<FriendRequestResponse>>> friendRequestState(String id,  Integer pageNumber, Integer pageSize) {
        Optional<UserEntity> user = userRepository.findById(id);
        if(user.isEmpty()) throw new NotFoundException("Not found user with id: " + id);
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<FriendshipEntity> friendshipEntities = friendshipRepository.getFriendRequestState(id, pageable);
            List<FriendshipEntity> fl = friendshipEntities.getContent();
            List<FriendRequestResponse> friendRequestResponses = new ArrayList<>();
            for(FriendshipEntity f : fl) {
                if(f.getSenderUserEntity().getUserId().equals(id)) {
                    FriendRequestResponse friendRequestResponse = new FriendRequestResponse();
                    friendRequestResponse.setUser(OverallUserMapper.entityToDto(f.getReceiverUserEntity()));
                    friendRequestResponse.setCreatedAt(f.getCreatedAt());
                    friendRequestResponse.setStatus(NotiFriendRequestEnum.SUCCESS);
                    friendRequestResponses.add(friendRequestResponse);
                } else if(f.getReceiverUserEntity().getUserId().equals(id)) {
                    FriendRequestResponse friendRequestResponse = new FriendRequestResponse();
                    friendRequestResponse.setUser(OverallUserMapper.entityToDto(f.getSenderUserEntity()));
                    friendRequestResponse.setCreatedAt(f.getCreatedAt());
                    friendRequestResponse.setStatus(NotiFriendRequestEnum.PENDING);
                    friendRequestResponses.add(friendRequestResponse);
                }
            }
//            for (UserEntity userEntity : fl) {
//                FriendshipEntity friendshipEntity = friendshipRepository.findFriendshipBySenderUserAndReceiverUser(userEntity, user.get());
//                if (friendshipEntity != null) {
//                    FriendRequestResponse friendRequestResponse = new FriendRequestResponse();
//                    friendRequestResponse.setUser(OverallUserMapper.entityToDto(userEntity));
//                    friendRequestResponse.setCreatedAt(friendshipEntity.getCreatedAt());
//                    friendRequestResponses.add(friendRequestResponse);
//                }
//            }
            Paginate<List<FriendRequestResponse>> dataResponse = new Paginate<>(
                    friendshipEntities.getNumber(),
                    friendshipEntities.getSize(),
                    friendshipEntities.getTotalElements(),
                    friendshipEntities.getTotalPages(),
                    friendshipEntities.isLast(),
                    friendRequestResponses
            );
            return ApiResponse.success(HttpStatus.OK, "Get received friend requests!", dataResponse);
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid param!");
        }
    }

//    @Override
//    public ApiResponse<FriendshipResponse> accept(Long friendshipId) {
//        FriendshipEntity friendship = friendshipRepository.findById(friendshipId).orElse(null);
//
//        if (friendship == null || !friendship.getStatus().equals(FriendStatusEnum.PENDING)) {
//            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid request", null);
//        }
//
//        friendship.setStatus(FriendStatusEnum.ACCEPTED);
//
//        FollowRequest follow = new FollowRequest();
//        follow.setFollowerId(friendship.getReceiverUserEntity().getUserId());
//        follow.setFollowingId(friendship.getSenderUserEntity().getUserId());
//        followService.friendshipFollow(follow);
//
//        FriendshipEntity updatedFriendship = friendshipRepository.save(friendship);
//
//        return ApiResponse.success(HttpStatus.OK, "Accept success!", friendshipMapper.entityToFriendshipResponse(updatedFriendship));
//    }
    @Override
    public ApiResponse<FriendshipResponse> accept(String senderUserId, String receiverUserId) {
        FriendshipEntity friendship = friendshipRepository.getFriendShipById(senderUserId, receiverUserId);

        if (friendship == null || !friendship.getStatus().equals(FriendStatusEnum.PENDING)) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid request", null);
        }

        friendship.setStatus(FriendStatusEnum.ACCEPTED);
        friendship.setCreatedAt(LocalDateTime.now());

        //!!!!!!!!!!
//        FollowRequest follow = new FollowRequest();
//        follow.setUserId(friendship.getSenderUserEntity().getUserId());
//        followService.friendshipFollow(follow);
        //!!!!!!!!!!
        FollowRequest follow = new FollowRequest();
        follow.setFollowerId(friendship.getSenderUserEntity().getUserId());
        follow.setFollowingId(friendship.getReceiverUserEntity().getUserId());
        followService.friendshipFollow(follow);
        FriendshipEntity updatedFriendship = friendshipRepository.save(friendship);

        FriendRequestResponse friendRequestResponse = new FriendRequestResponse();
        friendRequestResponse.setUser(OverallUserMapper.entityToDto(updatedFriendship.getReceiverUserEntity()));
        friendRequestResponse.setCreatedAt(updatedFriendship.getCreatedAt());
        friendRequestResponse.setStatus(NotiFriendRequestEnum.SUCCESS);

        simpMessagingTemplate.convertAndSendToUser(updatedFriendship.getSenderUserEntity().getUserId(),"/topic/friendship",friendRequestResponse);

        return ApiResponse.success(HttpStatus.OK, "Accept success!", friendshipMapper.entityToFriendshipResponse(updatedFriendship));
    }

    @Override
    public ApiResponse<FriendshipResponse> rejected(String senderUserId, String receiverUserId) {
        FriendshipEntity friendship = friendshipRepository.getFriendShipById(senderUserId, receiverUserId);

        if (friendship == null || !friendship.getStatus().equals(FriendStatusEnum.PENDING)) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid request", null);
        }

        friendship.setStatus(FriendStatusEnum.REJECTED);
        followService.deleteFollow(friendship.getSenderUserEntity().getUserId(), friendship.getReceiverUserEntity().getUserId());

        FriendshipEntity updatedFriendship = friendshipRepository.save(friendship);


        // send socket friend accept
        FriendRequestResponse friendRequestResponse = new FriendRequestResponse();
        friendRequestResponse.setUser(OverallUserMapper.entityToDto(updatedFriendship.getSenderUserEntity()));
        friendRequestResponse.setCreatedAt(updatedFriendship.getCreatedAt());
        friendRequestResponse.setStatus(NotiFriendRequestEnum.CANCEL);

        simpMessagingTemplate.convertAndSendToUser(updatedFriendship.getReceiverUserEntity().getUserId(),"/topic/friendship",friendRequestResponse);

        return ApiResponse.success(HttpStatus.OK, "Reject success!", friendshipMapper.entityToFriendshipResponse(updatedFriendship));
    }
//    @Override
//    public ApiResponse<FriendshipResponse> rejected(Long friendshipId) {
//        FriendshipEntity friendship = friendshipRepository.findById(friendshipId).orElse(null);
//
//        if (friendship == null || !friendship.getStatus().equals(FriendStatusEnum.PENDING)) {
//            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid request", null);
//        }
//
//        friendship.setStatus(FriendStatusEnum.REJECTED);
//        followService.deleteFollow(friendship.getSenderUserEntity().getUserId(), friendship.getReceiverUserEntity().getUserId());
//
//        FriendshipEntity updatedFriendship = friendshipRepository.save(friendship);
//
//        return ApiResponse.success(HttpStatus.OK, "Reject success!", friendshipMapper.entityToFriendshipResponse(updatedFriendship));
//    }
@Override
public ApiResponse<Paginate<List<FriendRequestResponse>>> getFriendsByUserIdPaginate(String userId, Integer pageNumber, Integer pageSize) {

    try {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        UserEntity user = userRepository.findUserEntityByUsername(userId);
//            Page<UserEntity> friendsSentToUser = friendshipRepository.findFriendsSentToUserPageable(user.getUserId(), pageable);
//            Page<UserEntity> friendsReceivedByUser = friendshipRepository.findFriendsReceivedByUser(user.getUserId(), pageable);
        Page<UserEntity> findFriend = friendshipRepository.findFriends(user.getUserId(), pageable);
        List<UserEntity> allFriends = findFriend.getContent();
//                    new ArrayList<>(friendsSentToUser.getContent());
//            allFriends.addAll(friendsReceivedByUser.getContent());

        List<FriendRequestResponse> friendResponses = new ArrayList<>();
//            int number = Math.max(friendsSentToUser.getNumber(), friendsReceivedByUser.getNumber());
//            int size = Math.max(friendsSentToUser.getSize(), friendsReceivedByUser.getSize());
//            long totalElements = friendsSentToUser.getTotalElements() + friendsReceivedByUser.getTotalElements();
//            int totalPages = Math.max(friendsSentToUser.getTotalPages(), friendsReceivedByUser.getTotalPages());
//            boolean isLast = friendsSentToUser.isLast() && friendsReceivedByUser.isLast();

        for (UserEntity friend : allFriends) {
            FriendRequestResponse friendResponse = new FriendRequestResponse();
            friendResponse.setUser(OverallUserMapper.entityToDto(friend));
            friendResponse.setCreatedAt(null);
            friendResponses.add(friendResponse);
        }

        Paginate<List<FriendRequestResponse>> dataResponse = new Paginate<>(
                findFriend.getNumber(),
                findFriend.getSize(),
                findFriend.getTotalElements(),
                findFriend.getTotalPages(),
                findFriend.isLast(),
                friendResponses
        );

        return ApiResponse.success(HttpStatus.OK, "List of friends", dataResponse);
    } catch (Exception e) {
        System.out.println("error: " + e.getMessage());
        return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid param!");
    }
}

    @Override
    public ApiResponse<Paginate<List<FriendRequestResponse>>> getFriendSuggestions(String userId, Integer pageNumber, Integer pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<UserEntity> friendSuggestions = friendshipRepository.findSuggestionsForUser(userId, pageable);
            List<UserEntity> users = friendSuggestions.getContent();
            List<FriendRequestResponse> suggestionResponses = new ArrayList<>();
            for (UserEntity suggestion : users) {
                // Ánh xạ từ UserEntity sang FriendRequestResponse
                FriendRequestResponse suggestionResponse = new FriendRequestResponse();
                suggestionResponse.setUser(OverallUserMapper.entityToDto(suggestion));
                suggestionResponse.setCreatedAt(null);
                suggestionResponses.add(suggestionResponse);
            }
            Paginate<List<FriendRequestResponse>> dataResponse = new Paginate<>(
                    friendSuggestions.getNumber(),
                    friendSuggestions.getSize(),
                    friendSuggestions.getTotalElements(),
                    friendSuggestions.getTotalPages(),
                    friendSuggestions.isLast(),
                    suggestionResponses
            );
            return ApiResponse.success(HttpStatus.OK , "List of friend suggestions", dataResponse);
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid param!");
        }
    }


    @Override
    public ApiResponse<List<OverallUserResponse>> getFriendsByUserId(String userId) {
        UserEntity userEnitty = userRepository.findUserEntityByUsername(userId);
        List<UserEntity> friendsSentToUser = friendshipRepository.findFriendsSentToUser(userEnitty.getUserId());
        List<UserEntity> friendsReceivedByUser = friendshipRepository.findFriendsReceivedByUser(userEnitty.getUserId());

        List<UserEntity> allFriends = new ArrayList<>(friendsSentToUser);
        allFriends.addAll(friendsReceivedByUser);
        List<OverallUserResponse> friendResponses = new ArrayList<>();

        for (UserEntity friend : allFriends) {
            OverallUserResponse overallUserResponse = OverallUserMapper.entityToDto(friend);
            friendResponses.add(overallUserResponse);
        }
        return ApiResponse.success(HttpStatus.OK , "List of friends", friendResponses);
    }
//
//    @Override
//    public ApiResponse<List<FriendRequestResponse>> getFriendSuggestions(String userId) {
//        List<UserEntity> friendSuggestions = friendshipRepository.findSuggestionsForUser(userId);
//        List<FriendRequestResponse> suggestionResponses = new ArrayList<>();
//        for (UserEntity suggestion : friendSuggestions) {
//            // Ánh xạ từ UserEntity sang FriendRequestResponse
//            FriendRequestResponse suggestionResponse = new FriendRequestResponse();
//            suggestionResponse.setUser(OverallUserMapper.entityToOverallUser(suggestion));
//            suggestionResponse.setCreatedAt(null);
//            suggestionResponses.add(suggestionResponse);
//        }
//
//        return ApiResponse.success(HttpStatus.OK , "List of friend suggestions", suggestionResponses);
//    }

    @Override
    public ApiResponse<ShortFriendshipResponse> getFriendshipStatus(String sourceId, String targetId) {
        try {
            FriendshipEntity statusFriendsEnum = friendshipRepository.checkFriendshipStatus(sourceId, targetId);
            UserEntity targetUser = userRepository.findById(targetId).get();
            OverallUserResponse overallUserResponse = OverallUserMapper.entityToDto(targetUser);

            ShortFriendshipResponse shortFriendshipResponse = new ShortFriendshipResponse();
            shortFriendshipResponse.setTargetUser(overallUserResponse);
            if(statusFriendsEnum != null) {
                if (statusFriendsEnum.getStatus().equals(FriendStatusEnum.ACCEPTED)) {
                    shortFriendshipResponse.setStatus(StatusFriendsEnum.FRIEND);
                } else if (statusFriendsEnum.getStatus().equals(FriendStatusEnum.PENDING)) {
                    if (statusFriendsEnum.getSenderUserEntity().getUserId().equals(sourceId)) {
                        shortFriendshipResponse.setStatus(StatusFriendsEnum.SENDER);
                    } else {
                        shortFriendshipResponse.setStatus(StatusFriendsEnum.RECEIVER);
                    }
                } else {
                    shortFriendshipResponse.setStatus(StatusFriendsEnum.NONE);
                }
            } else {
                shortFriendshipResponse.setStatus(StatusFriendsEnum.NONE);
            }
            return ApiResponse.success(HttpStatus.OK, "Check friendship status success", shortFriendshipResponse);
        } catch(Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Some thing went wrong");
        }
    }
}
