package com.kgat.service;

import com.kgat.entity.Comment;
import com.kgat.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public Page<Comment> getComments(Long articleNum) {
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");

        Page<Comment> comments = commentRepository.findAllByArticleNum(articleNum, pageable);


        return comments;
    }

    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }
}
