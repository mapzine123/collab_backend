package com.kgat.dto;

import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserSignupDTO {
    private String id;
    private String password;
}
