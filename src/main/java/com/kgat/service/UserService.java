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
