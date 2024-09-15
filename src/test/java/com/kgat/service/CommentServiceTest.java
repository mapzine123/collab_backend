package com.kgat.service;

import com.kgat.entity.Comment;
import com.kgat.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CommentServiceTest {
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("특정 게시글에 대한 댓글 목록을 가져온다.")
    void getComments() {
        Long articleId = 1L;
        Comment comment = new Comment(articleId, "abc", "와 개웃기네 ㅋㅋ");
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");

        commentRepository.save(comment);
        Page<Comment> comments = commentService.getComments(articleId, comment.getUserId(), pageable);

        assertThat(comments).isNotNull();
    }
}