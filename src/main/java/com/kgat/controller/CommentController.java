package com.kgat.controller;

import com.kgat.entity.Comment;
import com.kgat.entity.SubComment;
import com.kgat.repository.SubCommentRepository;
import com.kgat.service.CommentService;
import com.kgat.service.SubCommentService;
import com.kgat.vo.CommentData;
import com.kgat.vo.SubCommentData;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PreUpdate;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private SubCommentService subCommentService;
    private SubCommentRepository subCommentRepository;

    @GetMapping("/{articleId}")
    public ResponseEntity<Page<Comment>> getComment(@PathVariable("articleId") Long articleId, @RequestParam(required = false) String userId) {
        // 게시글에 대한 댓글들 리스트로 가져오기
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");

        Page<Comment> comments = commentService.getComments(articleId, userId, pageable);

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
    public ResponseEntity<Page<SubComment>> getSubComment(
            @PathVariable("commentId") Long commentId,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        Page<SubComment> subComments = subCommentService.getComments(commentId, userId, page, size, sortBy, direction);

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

    @PostMapping("/subComments/like")
    public ResponseEntity<?> likeSubComment(@RequestBody SubCommentData data) {
        try {
            SubComment subComment = subCommentService.likeSubComment(data.getSubCommentId(), data.getUserId());

            return ResponseEntity.ok(subComment);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the request");
        }
    }

    @PostMapping("/subComments/hate")
    public ResponseEntity<?> hateSubComment(@RequestBody SubCommentData data) {
        try {
            SubComment subComment = subCommentService.hateSubComment(data.getSubCommentId(), data.getUserId());

            return ResponseEntity.ok(subComment);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the request");
        }
    }

    @DeleteMapping("/subComments")
    public ResponseEntity<?> deleteSubComment(@RequestBody Map<String, Long> payload) {
        try {
            Long subCommentId = payload.get("subCommentId");
            Long commentId = payload.get("commentId");
            System.out.println(payload);
            if(subCommentId == null) {
                return ResponseEntity.badRequest().body("subCommentId is required");
            }
            subCommentService.deleteSubComment(commentId, subCommentId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/subComments")
    public ResponseEntity<SubComment> modifySubComment(@RequestBody Map<String, String> payload) {
        try {
            Long subCommentId = Long.parseLong(payload.get("subCommentId"));
            String subCommentText = payload.get("subCommentText");

            subCommentService.modifySubComment(subCommentId, subCommentText);

            return ResponseEntity.ok().build();
        } catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
