package com.kgat.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {
    @DisplayName("유저 객체가 만들어진다.")
    @Test
    void makeUser() {
        User user = new User("abc", "1234");

        assertThat(user).isNotNull();
    }
}