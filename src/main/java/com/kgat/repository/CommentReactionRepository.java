package com.kgat.repository;

import com.kgat.entity.CommentReaction;
import com.kgat.vo.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, Long> {

    Optional<CommentReaction> findByCommentIdAndUserId(long commentNum, String userId);

    int countByCommentIdAndReactionType(long commentNum, ReactionType reactionType);

    List<CommentReaction> findByCommentIdInAndUserId(List<Long> commentIds, String userId);

}
