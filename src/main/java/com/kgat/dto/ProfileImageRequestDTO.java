package com.kgat.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProfileImageRequestDTO {
    private String userId;
    private String imagePath;
    private String imageUrl;
}
