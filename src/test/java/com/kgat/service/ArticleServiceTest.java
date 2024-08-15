package com.kgat.service;

import com.kgat.entity.Article;
import com.kgat.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArticleServiceTest {

    @Autowired
    private ArticleService articleService;

    @Test
    @DisplayName("모든 게시글 정보를 가져온다.")
    void getAllArticle() {
        List<Article> articles = articleService.getAllArticle();

        assertThat(articles.size() == 20);
    }
}