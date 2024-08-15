package com.kgat.service;

import com.kgat.entity.User;
import com.kgat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class UserService {
    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private UserRepository userRepository;

    public User save(User user) {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            return null;
        }
    }

    public void saveUserProfileImage(User user, InputStream inputStream) throws IOException {
        // 파일 저장 후 저장 경로 반환
        String profileImagePath = fileStorageService.saveProfileImage(inputStream, user.getId());

        // 프로필 이미지 경로 DB에 업데이트
        userRepository.updateProfileImagePath(user.getId(), profileImagePath);

    }
}
