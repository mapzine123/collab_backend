package com.kgat.service;

import com.kgat.entity.Article;
import com.kgat.entity.ArticleReaction;
import com.kgat.entity.Comment;
import com.kgat.entity.CommentReaction;
import com.kgat.repository.CommentReactionRepository;
import com.kgat.repository.CommentRepository;
import com.kgat.vo.CommentData;
import com.kgat.vo.ReactionType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentReactionRepository commentReactionRepository;

    public Page<Comment> getComments(Long articleId, String userId, Pageable pageable) {
        // 게시글 id에 해당하는 모든 댓글을 페이징하여 조회
        Page<Comment> comments = commentRepository.findAllByArticleId(articleId, pageable);

        // userId가 있고, 댓글이 존재하는 경우에만 반응 정보 처리
        if(userId != null && !comments.isEmpty()) {
            // 조회된 모든 댓글의 ID 추출
            List<Long> commentIds = comments.getContent().stream()
                    .map(Comment::getCommentId)
                    .collect(Collectors.toList());

            // 추출한 댓글 id 리스트와 userId를 사용하여 해당 사용자의 반응 조회
            // 조회 결과를 CommentId를 키로 하는 map으로 변환해 빠른 접근이 가능하게 함
            Map<Long, CommentReaction> reactionMap = commentReactionRepository
                    .findByCommentIdInAndUserId(commentIds, userId)
                    .stream()
                    .collect(Collectors.toMap(CommentReaction::getCommentId, Function.identity()));

            // 각 댓글에 대해 사용자 반응 정보 설정
            comments.getContent().forEach(comment -> {
               CommentReaction reaction = reactionMap.get(comment.getCommentId());

               // 반응 타입에 따라 Like 또는 Hate 상태 설정
               if(reaction != null) {
                   if(ReactionType.LIKE.equals(reaction.getReactionType())) {
                       comment.setLike(true);
                   } else {
                       comment.setHate(true);
                   }
               }
            });
        }
        return comments;
    }

    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment updateComment(CommentData data) {
        try {
            commentRepository.updateComment(data.getCommentText(), data.getCommentId());

            Optional<Comment> comment = commentRepository.findById(data.getCommentId());
            System.out.println(comment);
            return comment.orElse(null);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public void deleteComment(Long commentId) throws Exception {
        try {
            commentRepository.deleteByCommentId(commentId);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public Comment likeComment(CommentData data) {
        // DB에서 reaction 찾아옴
        Optional<CommentReaction> reaction = commentReactionRepository.findByCommentIdAndUserId(data.getCommentId(), data.getUserId());
        Comment comment = commentRepository.findByCommentIdAndUserId(data.getCommentId(), data.getUserId());

        // 만약에 값이 있으면?
        if(reaction.isPresent()) {
            CommentReaction existingcommentReaction = reaction.get();

            // 만약에 그 리액션이 좋아요였으면?
            if(existingcommentReaction.getReactionType() == ReactionType.LIKE) {
                // 좋아요 토글(좋아요 해제)
                comment.toggleLike();

                // 리액션 삭제
                commentReactionRepository.delete(existingcommentReaction);
            } else {
                // 만약에 그 리액션이 싫어요였으면?
                // 싫어요 토글(싫어요 해제)
                comment.toggleHate();

                // 좋아요 토글(좋아요 추가)
                comment.toggleLike();

                // 리액션 타입 변경 [HATE -> LIKE]
                existingcommentReaction.setReactionType(ReactionType.LIKE);
                commentReactionRepository.save(existingcommentReaction);
            }
            // 만약에 값이 없으면??
        } else {
            // 새 좋아요 리액션 추가
            comment.toggleLike();
            commentReactionRepository.save(new CommentReaction(data.getCommentId(), data.getUserId(), ReactionType.LIKE));
        }

        // comment like, hate 카운트 업데이트
        comment.setLikeCount(commentReactionRepository.countByCommentIdAndReactionType(data.getCommentId(), ReactionType.LIKE));
        comment.setHateCount(commentReactionRepository.countByCommentIdAndReactionType(data.getCommentId(), ReactionType.HATE));
        commentRepository.save(comment);

        return comment;
    }

    @Transactional
    public Comment hateComment(CommentData data) {
        // DB에서 reaction 찾아옴
        Optional<CommentReaction> reaction = commentReactionRepository.findByCommentIdAndUserId(data.getCommentId(), data.getUserId());
        Comment comment = commentRepository.findByCommentId(data.getCommentId());

        // 만약에 값이 있으면?
        if(reaction.isPresent()) {
            CommentReaction existingcommentReaction = reaction.get();

            // 만약에 그 리액션이 싫어요였으면?
            if(existingcommentReaction.getReactionType() == ReactionType.HATE) {
                // 싫어요 토글(싫어요 해제)
                comment.toggleHate();

                // 리액션 삭제
                commentReactionRepository.delete(existingcommentReaction);
            } else {
                // 만약에 그 리액션이 좋아요였으면?
                // 좋아요 토글(좋아요 해제)
                comment.toggleLike();

                // 좋아요 토글(싫어요 추가)
                comment.toggleHate();

                // 리액션 타입 변경 [LIKE -> HATE]
                existingcommentReaction.setReactionType(ReactionType.HATE);
                commentReactionRepository.save(existingcommentReaction);
            }
            // 만약에 값이 없으면??
        } else {
            // 새 좋아요 리액션 추가
            comment.toggleHate();
            commentReactionRepository.save(new CommentReaction(data.getCommentId(), data.getUserId(), ReactionType.HATE));
        }

        // comment like, hate 카운트 업데이트
        comment.setLikeCount(commentReactionRepository.countByCommentIdAndReactionType(data.getCommentId(), ReactionType.LIKE));
        comment.setHateCount(commentReactionRepository.countByCommentIdAndReactionType(data.getCommentId(), ReactionType.HATE));
        commentRepository.save(comment);

        return comment;
    }

}
