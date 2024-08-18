package com.kgat.repository;

import com.kgat.entity.Reaction;
import com.kgat.vo.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    // ArticleNum과 UserId로 값 찾기
    Optional<Reaction> findByArticleNumAndUserId(long articleNum, String userId);
    
    // ArticleNum과 ReactionType로 식별하는 집계 쿼리
    int countByArticleNumAndReactionType(long articleNum, ReactionType reactionType);

}
