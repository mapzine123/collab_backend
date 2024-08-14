package com.kgat.repository;

import com.kgat.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("회원 가입 시 DB에 회원 정보가 들어가고, 다시 찾을 수 있음")
    void insertTest() {
        User newUser = new User("abe", "1234");
        userRepository.save(newUser);

        User findUser = userRepository.findById(newUser.getId()).orElse(null);
        assertThat(findUser).isNotNull();
        assertThat(findUser.getId()).isEqualTo("abe");
    }
}