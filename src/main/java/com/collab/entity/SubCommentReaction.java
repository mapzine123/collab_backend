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
        name="subCommentReactions",
        indexes = {
                @Index(name="idx_subCommentNum_userId", columnList = "subCommentId, userId")
        }
)
public class SubCommentReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subCommentReactionId;

    @Column(nullable = false)
    private Long subCommentId;

    @Column(nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType reactionType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubCommentReaction that = (SubCommentReaction) o;
        return Objects.equals(subCommentReactionId, that.subCommentReactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subCommentReactionId, subCommentId, userId, reactionType);
    }

    public SubCommentReaction(Long subCommentId, String userId, ReactionType reactionType) {
        this.subCommentId = subCommentId;
        this.userId = userId;
        this.reactionType = reactionType;
    }

    @Override
    public String toString() {
        return "SubCommentReaction{" +
                "subCommentReactionId=" + subCommentReactionId +
                ", subCommentId=" + subCommentId +
                ", userId='" + userId + '\'' +
                ", reactionType=" + reactionType +
                '}';
    }
}
