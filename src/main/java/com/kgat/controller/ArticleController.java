package com.kgat.controller;

import com.kgat.entity.Article;
import com.kgat.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public ResponseEntity<Page<Article>> getArticles(@RequestParam(defaultValue = "0") int page) {

        Page<Article> articlePage = articleService.getArticles(page);
        if(articlePage.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(articlePage);
    }

    @PostMapping
    public ResponseEntity<String> saveArticle(@RequestBody Article article) {
        Article savedArticle = articleService.saveArticle(article);

        System.out.println(article);

        if(savedArticle == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(savedArticle.toString());
        }
    }
}
