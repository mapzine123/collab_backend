package com.kgat.repository;


import com.kgat.entity.SubCommentReaction;
import com.kgat.vo.ReactionType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubCommentReactionRepository extends JpaRepository<SubCommentReaction, Long> {
    Optional<SubCommentReaction> findBySubCommentIdAndUserId(long subCommentNum, String userId);

    int countBySubCommentIdAndReactionType(long subCommentNum, ReactionType reactionType);
    List<SubCommentReaction> findBySubCommentIdInAndUserId(List<Long> subCommentIds, String userId);
}
