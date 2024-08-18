package com.kgat.entity;

import com.kgat.vo.ReactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public class Reaction {
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

    public Reaction(Long articleNum, String userId, ReactionType reactionType) {
        this.articleNum = articleNum;
        this.userId = userId;
        this.reactionType = reactionType;
    }
}
