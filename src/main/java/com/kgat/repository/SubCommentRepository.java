package com.kgat.repository;

import com.kgat.entity.SubComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubCommentRepository extends JpaRepository<SubComment, Long> {
    Page<SubComment> findByCommentId(Long commentId, Pageable pageable);

    SubComment findBySubCommentIdAndUserId(long subCommentId, String userId);
}
