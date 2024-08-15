package com.kgat.service;

import com.kgat.entity.User;
import com.kgat.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("입력한 id, pw를 DB에서 대조 후 가지고 온다.")
    void selectUser() {
        String inputId = "abc";
        String inputPw = "123";

        User user = new User("abc", "123", "aa");
        when(userRepository.findById("abc")).thenReturn(Optional.of(user));

        User findUser = authService.login(inputId, inputPw);
        assertThat(findUser).isNotNull();
        assertTrue(findUser.getPassword().equals(inputPw));

    }
}