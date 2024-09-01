package com.kgat.vo;

import com.kgat.entity.Comment;
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
    private String userId;

    public CommentData(long commentId, String userId) {
        this.commentId = commentId;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "CommentData{" +
                "commentId=" + commentId +
                ", commentText='" + commentText + '\'' +
                '}';
    }
}
