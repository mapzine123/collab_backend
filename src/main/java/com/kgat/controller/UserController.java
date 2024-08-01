package com.kgat.controller;

import com.kgat.entity.User;
import com.kgat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userService.save(user);
        if(savedUser == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(savedUser);
    }

}
