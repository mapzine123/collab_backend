package com.kgat.service;

import com.kgat.entity.Article;
import com.kgat.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    @Autowired
    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public List<Article> getAllArticle() {
        return articleRepository.findAll();
    }

    public Page<Article> getArticles(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return articleRepository.findAll(pageable);
    }

    public Article saveArticle(Article article) {
        try {
            return articleRepository.save(article);
        } catch (DataIntegrityViolationException e) {
            return null;
        }
    }
}
