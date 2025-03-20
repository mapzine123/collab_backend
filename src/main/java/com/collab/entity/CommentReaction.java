package com.collab.entity;

import com.collab.vo.ReactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name="commentReactions",
        indexes = {
                @Index(name="idx_commentNum_userId", columnList = "commentId, userId")
        }
)
public class CommentReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentReactionId;

    @Column(nullable = false)
    private Long commentId;

    @Column(nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType reactionType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentReaction that = (CommentReaction) o;
        return Objects.equals(commentReactionId, that.commentReactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentReactionId, commentId, userId, reactionType);
    }

    public CommentReaction(Long commentId, String userId, ReactionType reactionType) {
        this.commentId = commentId;
        this.userId = userId;
        this.reactionType = reactionType;
    }

    @Override
    public String toString() {
        return "CommentReaction{" +
                "commentReactionId=" + commentReactionId +
                ", commentId=" + commentId +
                ", userId='" + userId + '\'' +
                ", reactionType=" + reactionType +
                '}';
    }
}
