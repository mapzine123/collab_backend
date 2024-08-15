package com.kgat.repository;

import com.kgat.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {

    // 제목 검색
    Page<Article> findByArticleTitleContaining(String keyword, Pageable pageable);
}
