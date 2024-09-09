package com.kgat.service;

import com.kgat.entity.SubComment;
import com.kgat.repository.CommentRepository;
import com.kgat.repository.SubCommentRepository;
import com.kgat.vo.SubCommentData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class SubCommentService {
    @Autowired
    private SubCommentRepository subCommentRepository;

    @Autowired
    private CommentRepository commentRepository;

    public Page<SubComment> getComments(Long commentId) {
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "likeCount");

        Page<SubComment> subComments = subCommentRepository.findByCommentId(commentId, pageable);

        return subComments;
    }

    public SubComment saveSubComment(SubCommentData data) {
        SubComment subComment = new SubComment(data.getCommentId(), data.getSubCommentText(), data.getUserId());
        try {
            SubComment newSubComment = subCommentRepository.save(subComment);
            commentRepository.postSubComment(data.getCommentId());
            return newSubComment;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
