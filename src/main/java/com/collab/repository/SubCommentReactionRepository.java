package com.collab.repository;


import com.collab.entity.SubCommentReaction;
import com.collab.vo.ReactionType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubCommentReactionRepository extends JpaRepository<SubCommentReaction, Long> {
    Optional<SubCommentReaction> findBySubCommentIdAndUserId(long subCommentNum, String userId);

    int countBySubCommentIdAndReactionType(long subCommentNum, ReactionType reactionType);
    List<SubCommentReaction> findBySubCommentIdInAndUserId(List<Long> subCommentIds, String userId);
    void deleteAllBySubCommentId(long subCommentId);
}
