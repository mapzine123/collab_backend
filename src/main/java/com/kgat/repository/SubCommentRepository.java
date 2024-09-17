package com.kgat.repository;

import com.kgat.entity.SubComment;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubCommentRepository extends JpaRepository<SubComment, Long> {
    Page<SubComment> findByCommentId(Long commentId, Pageable pageable);

    SubComment findBySubCommentIdAndUserId(long subCommentId, String userId);

    @Modifying
    @Transactional
    @Query("UPDATE SubComment sc SET sc.subCommentText = :subCommentText WHERE sc.subCommentId = :subCommentId")
    void updateSubComment(@Param("subCommentText") String subCommentText, @Param("subCommentId") Long subCommentId);

}
