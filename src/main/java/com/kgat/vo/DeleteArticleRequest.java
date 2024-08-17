package com.kgat.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteArticleRequest {
    private String userId;
    private int articleNum;

    @Override
    public String toString() {
        return"DeleteArticleRequest{" +
                "userId='" + userId + '\'' +
            ", articleNum=" + articleNum +
            '}';
    }
}

