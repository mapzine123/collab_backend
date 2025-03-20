package com.collab.controller;

import com.collab.dto.UserPostProfileImageRequestDTO;
import com.collab.dto.UserResponseDTO;
import com.collab.dto.UserPostRequestDTO;
import com.collab.entity.User;
import com.collab.service.UserService;
import com.collab.dto.UserUpdatePasswordRequestDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "사용자 관련 API", description = "사용자 관련 CRUD 작업을 처리하는 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserPostRequestDTO userDto) {
        try {
            User savedUser = userService.save(userDto);
            UserResponseDTO response = new UserResponseDTO(
                    savedUser.getId(),
                    savedUser.getName(),
                    savedUser.getDepartment()
            );

            return ResponseEntity.ok(response);
        } catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch(Exception e) {
            log.error("사용자 생성 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/password")
    public ResponseEntity<User> updatePassword(@RequestBody UserUpdatePasswordRequestDto request) {
        try {
            userService.updateUserPassword(request.getUserId(), request.getPassword());
            return ResponseEntity.ok().build();
        } catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getUsers() {
        try {
            List<UserResponseDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch(Exception e) {
            log.error("사용자 목록 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/image")
    public ResponseEntity<String> updateProfileImage(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UserPostProfileImageRequestDTO request) {
        try {
            log.info("S3 프로필 이미지 업데이트 요청: {}", request.getUserId());
            userService.updateUserProfileImage(request.getUserId(), request.getImagePath(), request.getImageUrl());
            return ResponseEntity.ok("프로필 이미지가 성공적으로 업데이트 되었습니다.");
        } catch(Exception e) {
            log.error("프로필 이미지 업데이트 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("프로필 이미지 업데이트 중 오류가 발생했습니다.");
        }
    }

    // 프로필 정보 조회 엔드포인트
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestParam("userId") String userId) {
        try {
            return ResponseEntity.ok(userService.getUserProfile(userId));
        } catch(Exception e) {
            log.error("사용자 프로필 조회 중 오류 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자 프로필 조회 중 오류 발생");
        }
    }
}
