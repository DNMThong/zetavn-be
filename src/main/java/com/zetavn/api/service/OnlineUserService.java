package com.zetavn.api.service;

import java.util.List;

public interface OnlineUserService {
    public void addUser(String id);
    public void removeUser(String id);
    public List<String> getOnlineUsers();
    public boolean isUserOnline(String id);
}
