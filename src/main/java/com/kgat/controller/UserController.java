package com.kgat.controller;

import com.kgat.dto.UserSignupDTO;
import com.kgat.entity.User;
import com.kgat.service.UserService;
import com.kgat.dto.PasswordUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

import com.kgat.vo.Constants;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserSignupDTO userDto) {
        User savedUser = userService.save(userDto);
        if (savedUser == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/password")
    public ResponseEntity<User> updatePassword(@RequestBody PasswordUpdateDTO request) {
        try {
            userService.updateUserPassword(request.getUserId(), request.getPassword());
            return ResponseEntity.ok().build();
        } catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
