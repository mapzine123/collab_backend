package com.collab.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDTO {
    private Long commentId;
    private String commentText;
    private String userId;

    public CommentRequestDTO(long commentId, String userId) {
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
