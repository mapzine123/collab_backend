package com.kgat.service;

import com.kgat.dto.UserSignupDTO;
import com.kgat.entity.User;
import com.kgat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User save(UserSignupDTO userDto) {
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        User user = User.builder()
                        .id(userDto.getId())
                        .password(encodedPassword)
                        .build();
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            return null;
        }
    }

    public void saveUserProfileImage(User user) throws IOException {
        // 프로필 이미지 경로 DB에 업데이트
        userRepository.updateProfileImagePath(user.getProfileImagePath(), user.getId());
    }

    public void updateUserPassword(String password, String userId) {
        userRepository.updatePassword(password, userId);
    }

    public boolean authenticateUser(String userId, String password) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            return false;
        }

        return passwordEncoder.matches(password, user.getPassword());
    }
}
