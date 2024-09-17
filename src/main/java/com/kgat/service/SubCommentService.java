package com.kgat.service;

import com.kgat.entity.SubComment;
import com.kgat.entity.SubCommentReaction;
import com.kgat.repository.CommentRepository;
import com.kgat.repository.SubCommentReactionRepository;
import com.kgat.repository.SubCommentRepository;
import com.kgat.vo.Constant;
import com.kgat.vo.ReactionType;
import com.kgat.vo.SubCommentData;
import jakarta.persistence.EntityNotFoundException;
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
public class SubCommentService {
    @Autowired
    private SubCommentRepository subCommentRepository;

    @Autowired
    private SubCommentReactionRepository subCommentReactionRepository;

    @Autowired
    private CommentRepository commentRepository;

    public Page<SubComment> getComments(Long commentId, String userId, int page, int size, String sortBy, Sort.Direction direction) {
        // 페이징과 정렬 정보 생성
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // 대댓글 조회
        Page<SubComment> subComments = subCommentRepository.findByCommentId(commentId, pageable);

        if(userId != null && !subComments.isEmpty()) {
            // 모든 SubComment ID 추출
            List<Long> subCommentIds = subComments.getContent().stream()
                    .map(SubComment::getSubCommentId)
                    .collect(Collectors.toList());

            // 모든 관련 반응 조회
            List<SubCommentReaction> reactions = subCommentReactionRepository.findBySubCommentIdInAndUserId(subCommentIds, userId);

            // 반응을 map으로 변환
            Map<Long, SubCommentReaction> reactionMap = reactions.stream()
                    .collect(Collectors.toMap(SubCommentReaction::getSubCommentId, Function.identity()));

            // 각 subComment에 반응 정보 설정
            subComments.getContent().forEach(subComment -> {
               SubCommentReaction reaction = reactionMap.get(subComment.getSubCommentId());

               if(reaction != null) {
                   if(ReactionType.LIKE.equals(reaction.getReactionType())) {
                       subComment.setLike(true);
                   } else {
                       subComment.setHate(true);
                   }
               }
            });
        }

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

    @Transactional
    public SubComment likeSubComment(Long subCommentId, String userId) {
        // 대댓글 조회, 존재하지 않을시 예외 발생
        SubComment subComment = subCommentRepository.findById(subCommentId)
                .orElseThrow(() -> new EntityNotFoundException("SubComment not found"));

        // 사용자의 기존 반응 조회
        SubCommentReaction reaction = subCommentReactionRepository
                .findBySubCommentIdAndUserId(subCommentId, userId)
                .orElse(null);

        if(reaction == null) {
            // 새로운 좋아요 추가
            reaction = new SubCommentReaction(subCommentId, userId, ReactionType.LIKE);
            subComment.toggleLike(ReactionType.NEW);
        } else if(reaction.getReactionType() == ReactionType.LIKE) {
            // 기존 좋아요 취소
            subComment.toggleLike(ReactionType.CANCEL);
            subCommentReactionRepository.delete(reaction);
            return subCommentRepository.save(subComment);
        } else {
            // 싫어요를 좋아요로 변경
            subComment.toggleLike(ReactionType.CHANGE);
            reaction.setReactionType(ReactionType.LIKE);
        }

        subCommentReactionRepository.save(reaction);
        return subCommentRepository.save(subComment);
    }

    @Transactional
    public SubComment hateSubComment(Long subCommentId, String userId) {
        // 대댓글 조회, 존재하지 않을시 예외 발생
        SubComment subComment = subCommentRepository.findById(subCommentId)
                .orElseThrow(() -> new EntityNotFoundException("SubComment not found"));

        // 사용자의 기존 반응 조회
        SubCommentReaction reaction = subCommentReactionRepository
                .findBySubCommentIdAndUserId(subCommentId, userId)
                .orElse(null);

        if(reaction == null) {
            // 새로운 싫어요 추가
            reaction = new SubCommentReaction(subCommentId, userId, ReactionType.HATE);
            subComment.toggleHate(ReactionType.NEW);
        } else if(reaction.getReactionType() == ReactionType.HATE) {
            // 기존 싫어요 취소
            subComment.toggleHate(ReactionType.CANCEL);
            subCommentReactionRepository.delete(reaction);
            return subCommentRepository.save(subComment);
        } else {
            // 싫어요를 싫어요로 변경
            subComment.toggleHate(ReactionType.CHANGE);
            reaction.setReactionType(ReactionType.HATE);
        }

        subCommentReactionRepository.save(reaction);
        return subCommentRepository.save(subComment);
    }

    @Transactional
    public void deleteSubComment(long commentId, long subCommentId) {
        try {
            subCommentRepository.deleteById(subCommentId);
            subCommentReactionRepository.deleteAllBySubCommentId(subCommentId);
            commentRepository.deleteSubComment(commentId);
        } catch(Exception e) {
            throw e;
        }
    }

    @Transactional
    public void modifySubComment(Long subCommentId, String subCommentText) {
        try {
            subCommentRepository.updateSubComment(subCommentText, subCommentId);
        } catch(Exception e) {

        }
    }
}
