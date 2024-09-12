package com.kgat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name="subComments",
        indexes = {
                @Index(name="idx_commentId_userId", columnList = "commentId, userId")
        }
)
public class SubComment extends ReactionContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subCommentId;

    @Column(nullable = false)
    private Long commentId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String subCommentText;

    public SubComment(Long commentId, String subCommentText, String userId) {
        this.commentId = commentId;
        this.subCommentText = subCommentText;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "SubComment{" +
                "subCommentId=" + subCommentId +
                ", commentId=" + commentId +
                ", userId='" + userId + '\'' +
                ", subCommentText='" + subCommentText + '\'' +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubComment that = (SubComment) o;
        return Objects.equals(subCommentId, that.subCommentId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(subCommentId);
    }
}
