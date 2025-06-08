package com.whatsapp.service;

import com.whatsapp.model.User;
import java.util.List;

public interface UserService {
    User getUserById(Long id);
    User getUserByUsername(String username);
    List<User> getContacts(Long userId);
    List<User> searchContacts(Long userId, String searchTerm);
    void updateUserStatus(Long userId, boolean isOnline);
} 