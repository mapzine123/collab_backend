package com.kgat.controller;

import com.kgat.entity.User;
import com.kgat.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(("/api/auth"))
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<User> Login(@RequestBody User user) {
        User findUser = authService.login(user.getId(), user.getPassword());

        if(findUser != null) {
            return ResponseEntity.ok(findUser);
        } else {
            return ResponseEntity.badRequest().build();
        }

    }
}
