package com.collab.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ArticleData {
    private final String userId;
    private final long articleId;

    @Override
    public String toString() {
        return"ArticleData{" +
                "userId='" + userId + '\'' +
            ", articleId=" + articleId +
            '}';
    }
}

