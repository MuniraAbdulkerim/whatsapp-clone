package com.whatsappclone.controller;

import com.whatsappclone.model.User;
import com.whatsappclone.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User registeredUser = authService.registerUser(
                user.getPhone(),
                user.getUsername(),
                user.getPassword());
            return ResponseEntity.ok(registeredUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        boolean isAuthenticated = authService.authenticate(
            user.getUsername(),
            user.getPassword());
        if (isAuthenticated) {
            // Optionally, you can return the user object or a token here
            return ResponseEntity.ok().body("{\"status\":\"success\"}");
        } else {
            return ResponseEntity.status(401).body("{\"status\":\"error\",\"message\":\"Invalid credentials\"}");
        }
    }
}