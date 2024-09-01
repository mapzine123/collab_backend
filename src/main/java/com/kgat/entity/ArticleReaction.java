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
        name="reactions",
        indexes = {
                @Index(name="idx_articleNum_userId", columnList = "articleNum, userId")
        }
    )
public class ArticleReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reactionId;

    @Column(nullable = false)
    private Long articleNum;

    @Column(nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType reactionType;

    public ArticleReaction(Long articleNum, String userId, ReactionType reactionType) {
        this.articleNum = articleNum;
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
        return Objects.hash(reactionId, articleNum, userId, reactionType);
    }
}
