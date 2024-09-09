package com.kgat.controller;

import com.kgat.entity.Comment;
import com.kgat.entity.SubComment;
import com.kgat.service.CommentService;
import com.kgat.service.SubCommentService;
import com.kgat.vo.CommentData;
import com.kgat.vo.SubCommentData;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private SubCommentService subCommentService;

    @GetMapping("/{articleNum}")
    public ResponseEntity<Page<Comment>> getComment(@PathVariable("articleNum") Long articleNum) {
        // 게시글에 대한 댓글들 리스트로 가져오기
        Page<Comment> comments = commentService.getComments(articleNum);

        if(comments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<Comment> writeComment(@RequestBody Comment comment) {
        try {
            Comment savedComment = commentService.saveComment(comment);
            return ResponseEntity.ok(savedComment);
        } catch(DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping
    public ResponseEntity<Comment> modifyComment(@RequestBody CommentData data) {
        try {
            Comment comment = commentService.updateComment(data);

            if(comment == null) {
                throw new Exception();
            }

            return ResponseEntity.ok(comment);
        } catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Comment> deleteComment(@PathVariable Long commentId) {
        try {
            commentService.deleteComment(commentId);

            return ResponseEntity.ok().build();
        } catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/reaction/like")
    public ResponseEntity<Comment> likeArticle(@RequestBody CommentData data) {
        Comment comment = commentService.likeComment(data);

        return ResponseEntity.ok(comment);
    }

    @PostMapping("/reaction/hate")
    public ResponseEntity<Comment> hateArticle(@RequestBody CommentData data) {
        Comment comment = commentService.hateComment(data);

        return ResponseEntity.ok(comment);
    }

    @GetMapping("/subComments/{commentId}")
    public ResponseEntity<Page<SubComment>> getSubComment(@PathVariable("commentId") Long commentId) {
        Page<SubComment> subComments = subCommentService.getComments(commentId);

        if(subComments.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(subComments);
        }
    }

    @PostMapping("/subComments")
    public ResponseEntity<SubComment> addSubComment(@RequestBody SubCommentData data) {
        try {
            SubComment subcomment = subCommentService.saveSubComment(data);
            return ResponseEntity.ok(subcomment);
        } catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
