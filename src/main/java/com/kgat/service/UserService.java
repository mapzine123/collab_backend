package com.kgat.service;

import com.kgat.dto.ProfileImageRequestDTO;
import com.kgat.dto.UserResponseDTO;
import com.kgat.dto.UserSignupDTO;
import com.kgat.entity.User;
import com.kgat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public User save(UserSignupDTO userDto) {
        // 중복 ID 검사 수행
        if(userRepository.existsById(userDto.getId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        User user = User.builder()
                        .id(userDto.getId())
                        .password(encodedPassword)
                        .name(userDto.getName())
                        .department(userDto.getDepartment())
                        .build();
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("사용자 저장 중 오류가 발생했습니다.");
        }
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getName(),
                        user.getDepartment()
                )).collect(Collectors.toList());
    }

    @Transactional
    public void updateUserPassword(String userId, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 비밀번호 암호화 후 업데이트
        String encodedPassword = passwordEncoder.encode(password);
        userRepository.updatePassword(userId, encodedPassword);

    }

    public boolean authenticateUser(String userId, String password) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            return false;
        }

        return passwordEncoder.matches(password, user.getPassword());
    }

    // 사용자 정보 조회 메서드
    @Transactional(readOnly = true)
    public User findById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    @Transactional
    public void updateUserProfileImage(String userId, String imagePath, String imageUrl) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));

        // S3 이미지 경로 저장
        user.setImagePath(imagePath);
        user.setImageUrl(imageUrl);
        userRepository.save(user);

        log.info("사용자 S3 이미지 정보가 업데이트되었습니다: {}, 경로: {}", userId, imagePath);
    }

    public ProfileImageRequestDTO getUserProfile(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));

        return ProfileImageRequestDTO.builder()
                .userId(user.getId())
                .imagePath(user.getImagePath())
                .imageUrl((user.getImageUrl()))
                .build();
    }
}
