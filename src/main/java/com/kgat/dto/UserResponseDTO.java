package com.kgat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class UserResponseDTO {
    private String id;          // 아이디
    private String name;        // 실명
    private String department;  // 부서
}
