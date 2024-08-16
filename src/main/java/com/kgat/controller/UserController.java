package com.kgat.controller;

import com.kgat.entity.User;
import com.kgat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

import com.kgat.vo.Constant;


@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userService.save(user);
        if (savedUser == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/image")
    public ResponseEntity<String> uploadProfileImage(@RequestParam("userId") String userId, @RequestParam("file") MultipartFile file, @RequestParam("fileExtension") String fileExtension) throws IOException {
        try {
            String fileName = userId + "." + fileExtension;

            Path filePath = Paths.get(Constant.PROFILE_PASS + fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "http://localhost:8080/api/users/profileImage/" + fileName;

            User user = new User(userId, fileUrl);

            // 파일 업로드 및 저장
            userService.saveUserProfileImage(user);

            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Filed to upload file.");
        }
    }

    @GetMapping("/profileImage/{fileName:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable("fileName") String fileName) throws IOException {
        Path file = Paths.get(Constant.PROFILE_PASS).resolve(fileName).normalize();
        Resource fileResource = new FileSystemResource(file.toFile());

        if(fileResource.exists()) {
            return ResponseEntity.ok().body(fileResource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
