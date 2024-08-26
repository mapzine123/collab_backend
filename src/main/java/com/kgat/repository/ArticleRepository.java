package com.kgat.repository;

import com.kgat.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    // 제목 검색
    Page<Article> findByArticleTitleContaining(String keyword, Pageable pageable);

    // 작성자로 검색
    Page<Article> findByArticleWriter(String articleWriter, Pageable pageable);

    // 작성자명과 게시글 번호로 삭제
    void deleteByArticleWriterAndArticleNum(String userId, Long articleNum);

    Article findByArticleNum(Long articleNum);
}
