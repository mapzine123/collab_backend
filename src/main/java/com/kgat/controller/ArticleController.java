package com.kgat.controller;

import com.kgat.entity.Article;
import com.kgat.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public ResponseEntity<List<Article>> getArticles() {
        List<Article> allArticles = articleService.getAllArticle();

        if(allArticles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(allArticles);
    }
}
