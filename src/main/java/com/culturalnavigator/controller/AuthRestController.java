package com.culturalnavigator.controller;

import com.culturalnavigator.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthRestController {

    private final UserService userService;

    @GetMapping("/api/auth/check-username")
    public Map<String, Object> checkUsername(@RequestParam String username) {
        return Map.of("available", userService.usernameAvailable(username));
    }

    @GetMapping("/api/auth/check-email")
    public Map<String, Object> checkEmail(@RequestParam String email) {
        return Map.of("available", userService.emailAvailable(email));
    }
}
