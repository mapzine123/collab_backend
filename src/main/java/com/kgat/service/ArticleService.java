package com.kgat.service;

import com.kgat.entity.Article;
import com.kgat.repository.ArticleRepository;
import com.kgat.vo.DeleteArticleRequest;
import jakarta.transaction.Transactional;
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

    public Page<Article> getArticles(int page, String search) {
        Pageable pageable = PageRequest.of(page, 10);
        if(search.trim().equals("")) {
            return articleRepository.findAll(pageable);
        } else {
            return articleRepository.findByArticleTitleContaining(search, pageable);
        }
    }

    public Article saveArticle(Article article) {
        try {
            return articleRepository.save(article);
        } catch (DataIntegrityViolationException e) {
            return null;
        }
    }

    public Page<Article> getArticlesByWriter(String writer, int page) {
        Pageable pageable = PageRequest.of(page, 5);
        return articleRepository.findByArticleWriter(writer, pageable);
    }

    @Transactional
    public void deleteArticle(DeleteArticleRequest request) {
        articleRepository.deleteByArticleWriterAndArticleNum(request.getUserId(), request.getArticleNum());
    }

    public void updateArticle(Article article) {
        boolean isUpdated = false;

        try {
            Article prevArticle = articleRepository.findByArticleNum(article.getArticleNum());

            if(!article.getArticleTitle().equals(prevArticle.getArticleTitle())) {
                prevArticle.setArticleTitle(article.getArticleTitle());
                isUpdated = true;
            }

            if(!article.getArticleContent().equals(prevArticle.getArticleContent())) {
                prevArticle.setArticleContent(prevArticle.getArticleContent());
                isUpdated = true;
            }
            if(isUpdated) {
                articleRepository.save(prevArticle);
            }

        } catch(DataIntegrityViolationException e) {

        }
    }
}
