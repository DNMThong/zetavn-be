package com.zetavn.api.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class OnlineUserServiceImpl implements OnlineUserService {
    private List<String> onlineUsers = new CopyOnWriteArrayList<>();

    @Override
    public void addUser(String id) {
        this.onlineUsers.add(id);
    }

    @Override
    public void removeUser(String id) {
        this.onlineUsers.remove(id);
    }

    @Override
    public List<String> getOnlineUsers() {
        return this.onlineUsers;
    }

    @Override
    public boolean isUserOnline(String id) {
        return onlineUsers.contains(id);
    }
}
