package com.kgat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name="users")
public class User {
    public User(String id) {
        this.id = id;
    }

    @Id
    @Column(nullable = false)
    private String id; // 로그인용 아이디

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name; // 실명

    @Column(nullable = false)
    private String department; // 부서명
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
