package com.collab.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.collab.vo.ReactionType;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
public class ReactionContent extends Content implements Toggleable {
    @Column(nullable = false)
    private int likeCount;

    @Column(nullable = false)
    private int hateCount;

    @Transient
    @JsonProperty("isLike")
    private boolean isLike = false;

    @Transient
    @JsonProperty("isHate")
    private boolean isHate = false;

    @Column(nullable = false)
    private int replyCount = 0;

    @Override
    public void toggleLike() {
        if(isLike) {
            // 이미 좋아요를 누른 상태에서 클릭하면
            // 좋아요 비활성화, 카운트 감소
            decrementLikeCount();
            this.isLike = false;
        } else {
            // 좋아요를 누르지 않았던 상태에서 클릭하면
            // 좋아요 활성화, 카운트 증가
            incrementLikeCount();
            this.isLike = true;

            // 싫어요 활성화 상태면
            // 비활성화, 카운트 감소
            if(isHate) {
                decrementHateCount();
                this.isHate = false;
            }
        }
    }

    public void toggleLike(ReactionType sign) {
        if(sign.equals(ReactionType.NEW)) {
            isLike = true;
            incrementLikeCount();
        } else if(sign.equals(ReactionType.CANCEL)) {
            isLike = false;
            decrementLikeCount();
        } else if(sign.equals(ReactionType.CHANGE)) {
            isLike = true;
            isHate = false;
            incrementLikeCount();
            decrementHateCount();
        }
    }

    public void toggleHate(ReactionType sign) {
        if(sign.equals(ReactionType.NEW)) {
            isHate = true;
            incrementHateCount();
        } else if(sign.equals(ReactionType.CANCEL)) {
            isHate = false;
            decrementHateCount();
        } else if(sign.equals(ReactionType.CHANGE)) {
            isHate = true;
            isLike = false;
            decrementLikeCount();
            incrementHateCount();
        }
    }

    public ReactionContent(int likeCount, int hateCount, boolean isLike, boolean isHate) {
        this.likeCount = likeCount;
        this.hateCount = hateCount;
        this.isLike = isLike;
        this.isHate = isHate;
    }

    @Override
    public void toggleHate() {
        if(isHate) {
            // 이미 싫어요를 누른 상태에서 클릭하면
            // 싫어요 비활성화, 카운트 감소
            decrementHateCount();
            isHate = false;
        } else {
            // 싫어요를 누르지 않은 상태에서 클릭하면
            // 싫어요 활성화, 카운트 증가
            incrementHateCount();
            isHate = true;
        }

        if(isLike) {
            // 좋아요가 활성화 된 상태라면
            // 좋아요 비활성화, 좋아요 카운트 감소
            decrementLikeCount();
            isLike = false;
        }
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }

    public void incrementHateCount() {
        this.hateCount++;
    }

    public void decrementHateCount() {
        this.hateCount = Math.max(0, this.hateCount - 1);
    }

    public void incrementReplyCount() {
        this.replyCount++;
    }

    public void decrementReplyCount() {
        this.replyCount = Math.max(0, this.replyCount - 1);
    }
}
