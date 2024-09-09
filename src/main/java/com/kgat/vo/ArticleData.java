package com.kgat.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleData {
    private String userId;
    private long articleId;

    @Override
    public String toString() {
        return"ArticleData{" +
                "userId='" + userId + '\'' +
            ", articleId=" + articleId +
            '}';
    }
}

