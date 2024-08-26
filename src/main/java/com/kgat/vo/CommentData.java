package com.kgat.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentData {
    private Long commentId;
    private String commentText;

    @Override
    public String toString() {
        return "CommentData{" +
                "commentId=" + commentId +
                ", commentText='" + commentText + '\'' +
                '}';
    }
}
