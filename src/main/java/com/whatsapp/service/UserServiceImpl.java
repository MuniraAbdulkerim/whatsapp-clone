package com.whatsapp.service;

import com.whatsapp.model.User;
import com.whatsapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<User> getContacts(Long userId) {
        // In a real application, you would have a separate contacts table
        // For this example, we'll return all users except the current user
        return userRepository.findAll().stream()
                .filter(user -> !user.getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> searchContacts(Long userId, String searchTerm) {
        return userRepository.findAll().stream()
                .filter(user -> !user.getId().equals(userId) &&
                        (user.getUsername().toLowerCase().contains(searchTerm.toLowerCase()) ||
                         user.getFullName().toLowerCase().contains(searchTerm.toLowerCase())))
                .collect(Collectors.toList());
    }

    @Override
    public void updateUserStatus(Long userId, boolean isOnline) {
        User user = getUserById(userId);
        user.setOnline(isOnline);
        userRepository.save(user);
    }
} 