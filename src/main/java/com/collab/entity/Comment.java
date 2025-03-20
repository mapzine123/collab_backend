package com.collab.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name="comments",
        indexes = {
                @Index(name="comment_articleId_userId", columnList = "articleId, userId")
        }
)
public class Comment extends ReactionContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(nullable = false)
    private String commentText;

    @Column(nullable = false)
    private Long articleId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private int subCommentCount = 0;

    @Transient
    @JsonProperty("subComments")
    private List<SubComment> subComments = new ArrayList<SubComment>();

    @Transient
    @JsonProperty("isModified")
    private boolean isModified = false;

    public Comment(Long articleId, String userId, String commentText) {
        this.articleId = articleId;
        this.userId = userId;
        this.commentText = commentText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return commentId.equals(comment.commentId);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", commentText='" + commentText + '\'' +
                ", articleId=" + articleId +
                ", userId='" + userId + '\'' +
                ", replyCount=" + subCommentCount +
                ", isModified=" + isModified +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentId, commentText, articleId, userId, subCommentCount);
    }
}

