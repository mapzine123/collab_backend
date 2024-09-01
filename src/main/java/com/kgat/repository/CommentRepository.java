package com.kgat.repository;

import com.kgat.entity.Comment;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByArticleNum(Long articleNum, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Comment c SET c.commentText = :commentText WHERE c.commentId = :commentId")
    void updateComment(@Param("commentText") String commentText, @Param("commentId") Long commentId);

    void deleteByCommentId(Long commentId);

    Page<Comment> findAllByCommentId(Long commentId, Pageable pageable);

    Comment findByCommentId(Long commentId);

    Comment findByCommentIdAndUserId(Long commentId, String userId);
}
