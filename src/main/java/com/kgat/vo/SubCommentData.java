package com.kgat.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubCommentData {
    private long commentId;
    private String userId;
    private String subCommentText;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubCommentData that = (SubCommentData) o;
        return commentId == that.commentId && Objects.equals(userId, that.userId) && Objects.equals(subCommentText, that.subCommentText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentId, userId, subCommentText);
    }
}
