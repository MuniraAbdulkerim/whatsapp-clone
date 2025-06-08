package com.whatsappclone.service;

import com.whatsappclone.model.User;
import com.whatsappclone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User registerUser(String phone, String username, String password) {
        if (userRepository.existsByUsernameOrPhone(username, phone)) {
            throw new RuntimeException("Username or phone already exists");
        }
        
        User user = new User();
        user.setPhone(phone);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        
        return userRepository.save(user);
    }
    
    public boolean authenticate(String username, String password) {
        return userRepository.findByUsername(username)
            .map(user -> passwordEncoder.matches(password, user.getPassword()))
            .orElse(false);
    }
}