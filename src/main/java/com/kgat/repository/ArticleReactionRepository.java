package com.kgat.repository;

import com.kgat.entity.ArticleReaction;
import com.kgat.vo.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleReactionRepository extends JpaRepository<ArticleReaction, Long> {
    // ArticleNum과 UserId로 값 찾기
    Optional<ArticleReaction> findByArticleNumAndUserId(long articleNum, String userId);
    
    // ArticleNum과 ReactionType로 식별하는 집계 쿼리
    int countByArticleNumAndReactionType(long articleNum, ReactionType reactionType);

}
