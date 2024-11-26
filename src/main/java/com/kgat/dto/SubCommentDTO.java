package com.kgat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubCommentDTO {
    private long subCommentId;
    private long commentId;
    private String userId;
    private String subCommentText;

    public SubCommentDTO(long commentId, String userId, String subCommentText) {
        this.commentId = commentId;
        this.userId = userId;
        this.subCommentText = subCommentText;
    }

    public SubCommentDTO(long subCommentId, String userId) {
        this.subCommentId = subCommentId;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "SubCommentData{" +
                "subCommentId=" + subCommentId +
                ", commentId=" + commentId +
                ", userId='" + userId + '\'' +
                ", subCommentText='" + subCommentText + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentId, userId, subCommentText);
    }
}
