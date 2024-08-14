package com.kgat.service;

import com.kgat.entity.User;
import com.kgat.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("회원가입 성공")
    void saveSuccessed() {
        User user = new User("user1", "1234");

        when(userRepository.save(user)).thenReturn(user);

        User saveUser = userService.save(user);

        assertThat(saveUser).isNotNull();
        assertThat(saveUser.getId()).isEqualTo("user1");

    }

    @Test
    @DisplayName("회원가입 실패")
    void saveFailed() {
        User user = new User("user1", "1234");

        when(userRepository.save(user)).thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        User saveUser = userService.save(user);

        assertThat(saveUser).isNull();
    }
}