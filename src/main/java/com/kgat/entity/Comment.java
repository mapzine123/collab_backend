package com.kgat.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name="comments",
        indexes = {
                @Index(name="comment_articleNum_userId", columnList = "articleNum, userId")
        }
)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(nullable = false)
    private String commentText;

    @Column(nullable = false)
    private Long articleNum;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private int replyCount = 0;

    @Column(nullable = false)
    private int likeCount = 0;

    @Column(nullable = false)
    private int hateCount = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime updatedAt = null;

    @Transient
    @JsonProperty("isModified")
    private boolean isModified = false;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


    public Comment(Long articleNum, String userId, String commentText) {
        this.articleNum = articleNum;
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
                ", articleNum=" + articleNum +
                ", userId='" + userId + '\'' +
                ", replyCount=" + replyCount +
                ", likeCount=" + likeCount +
                ", hateCount=" + hateCount +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isModified=" + isModified +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentId, commentText, articleNum, userId, replyCount, likeCount, hateCount);
    }
}
