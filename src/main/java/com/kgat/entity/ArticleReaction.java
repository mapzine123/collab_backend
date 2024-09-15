package com.kgat.entity;

import com.kgat.vo.ReactionType;
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
        name="articleReactions",
        indexes = {
                @Index(name="idx_articleId_userId", columnList = "articleId, userId")
        }
    )
public class ArticleReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reactionId;

    @Column(nullable = false)
    private Long articleId;

    @Column(nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType reactionType;

    public ArticleReaction(Long articleId, String userId, ReactionType reactionType) {
        this.articleId = articleId;
        this.userId = userId;
        this.reactionType = reactionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArticleReaction articleReaction = (ArticleReaction) o;
        return reactionId.equals(articleReaction.reactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reactionId, articleId, userId, reactionType);
    }
}
