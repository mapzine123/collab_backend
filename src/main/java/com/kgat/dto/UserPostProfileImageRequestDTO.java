package com.kgat.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserPostProfileImageRequestDTO {
    private String userId;
    private String imagePath;
    private String imageUrl;
}
