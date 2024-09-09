package com.kgat.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
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

    @Override
    public void toggleLike() {
        if(isLike) {
            // 이미 좋아요를 누른 상태에서 클릭하면
            // 좋아요 비활성화, 카운트 감소
            likeCount--;
            isLike = false;
        } else {
            // 좋아요를 누르지 않았던 상태에서 클릭하면
            // 좋아요 활성화, 카운트 증가
            likeCount++;
            isLike = true;

            // 싫어요 활성화 상태면
            // 비활성화, 카운트 감소
            if(isHate) {
                hateCount--;
                isHate = false;
            }
        }
    }

    @Override
    public void toggleHate() {
        if(isHate) {
            // 이미 싫어요를 누른 상태에서 클릭하면
            // 싫어요 비활성화, 카운트 감소
            hateCount--;
            isHate = false;
        } else {
            // 싫어요를 누르지 않은 상태에서 클릭하면
            // 싫어요 활성화, 카운트 증가
            hateCount++;
            isHate = true;
        }

        if(isLike) {
            // 좋아요가 활성화 된 상태라면
            // 좋아요 비활성화, 좋아요 카운트 감소
            likeCount--;
            isLike = false;
        }
    }
}
