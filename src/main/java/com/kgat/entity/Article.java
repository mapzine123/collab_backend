package com.kgat.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="articles")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleNum;

    @Column(nullable = false)
    private String articleTitle;

    @Column(nullable = true)
    private String articleContent;

    @Column(nullable = false)
    private String articleWriter;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private int likeCount;

    @Column(nullable = false)
    private int commentCount;

    @Column(nullable = false)
    private int hateCount;

    @Transient
    @JsonProperty("isLike")
    private boolean isLike = false;

    @Transient
    @JsonProperty("isHate")
    private boolean isHate = false;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Article{" +
                "articleNum=" + articleNum +
                ", articleTitle='" + articleTitle + '\'' +
                ", articleContent='" + articleContent + '\'' +
                ", articleWriter='" + articleWriter + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", viewCount=" + viewCount +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                ", hateCount=" + hateCount +
                ", isLike=" + isLike +
                ", isHate=" + isHate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }

        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        Article article = (Article) o;
        return Objects.equals(articleNum, article.articleNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(articleNum);
    }

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
