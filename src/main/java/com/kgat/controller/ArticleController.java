package com.kgat.controller;

import com.kgat.entity.Article;
import com.kgat.service.ArticleService;
import com.kgat.vo.ArticleData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @GetMapping
    public ResponseEntity<Page<Article>> getArticles(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue="") String search, @RequestParam(defaultValue="") String userId) {

        Page<Article> articlePage = articleService.getArticles(page, search, userId);
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

    @GetMapping("/user")
    public ResponseEntity<Page<Article>> getUserArticles(@RequestParam int page, @RequestParam String userId) {
        Page<Article> articlePage = articleService.getArticlesByWriter(userId, page);

        if(articlePage.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(articlePage);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteArticle(@RequestBody ArticleData data) {
        try {
            articleService.deleteArticle(data);
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

    @PostMapping("/reaction/like")
    public ResponseEntity<Article> likeArticle(@RequestBody ArticleData data) {
        Article article = articleService.likeArticle(data);

        return ResponseEntity.ok(article);
    }

    @PostMapping("/reaction/hate")
    public ResponseEntity<Article> hateArticle(@RequestBody ArticleData data) {
        Article article = articleService.hateArticle(data);

        return ResponseEntity.ok(article);
    }


}
