package com.kgat.controller;

import com.kgat.entity.Article;
import com.kgat.service.ArticleService;
import com.kgat.vo.DeleteArticleRequest;
import org.apache.coyote.Response;
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
    public ResponseEntity<Page<Article>> getArticles(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue="") String search) {

        Page<Article> articlePage = articleService.getArticles(page, search);
        if(articlePage.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(articlePage);
    }

    @PostMapping
    public ResponseEntity<String> saveArticle(@RequestBody Article article) {
        Article savedArticle = articleService.saveArticle(article);

        if(savedArticle == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(savedArticle.toString());
        }
    }

    @GetMapping
    @RequestMapping("/user")
    public ResponseEntity<Page<Article>> getUserArticles(@RequestParam int page, @RequestParam String userId) {
        Page<Article> articlePage = articleService.getArticlesByWriter(userId, page);

        if(articlePage.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(articlePage);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteArticle(@RequestBody DeleteArticleRequest request) {
        try {
            articleService.deleteArticle(request);
            return ResponseEntity.ok("DELETE SUCCESS");
        } catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping
    public ResponseEntity<String> updateArticle(@RequestBody Article article) {
        try {
            articleService.updateArticle(article);
            return ResponseEntity.ok("UPDATE SUCCESS");
        } catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }
}
