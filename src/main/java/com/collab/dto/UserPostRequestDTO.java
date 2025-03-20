package com.collab.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class UserPostRequestDTO {
    @NotBlank(message = "아이디는 필수 입력값입니다")
    private String id;

    @NotBlank(message = "비밀번호는 필수 입력값입니다")
    private String password;

    @NotBlank(message = "이름은 필수 입력값입니다")
    private String name;

    @NotBlank(message = "부서는 필수 입력값입니다")
    private String department;
}
