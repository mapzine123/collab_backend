package com.collab.repository;

import com.collab.entity.ArticleReaction;
import com.collab.vo.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleReactionRepository extends JpaRepository<ArticleReaction, Long> {
    // ArticleId와 UserId로 값 찾기
    Optional<ArticleReaction> findByArticleIdAndUserId(long articleId, String userId);
    
    // ArticleId와 ReactionType로 식별하는 집계 쿼리
    int countByArticleIdAndReactionType(long articleId, ReactionType reactionType);

    List<ArticleReaction> findByArticleIdInAndUserId(List<Long> articleIds, String userId);
}
