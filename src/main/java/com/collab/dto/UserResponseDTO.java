package com.collab.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private String id;          // 아이디
    private String name;        // 실명
    private String department;  // 부서
}
