package com.kgat.service;

import com.kgat.entity.Article;
import com.kgat.entity.Reaction;
import com.kgat.repository.ArticleRepository;
import com.kgat.repository.ReactionRepository;
import com.kgat.vo.ArticleData;
import com.kgat.vo.ReactionType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ReactionRepository reactionRepository;

    @Autowired
    public ArticleService(ArticleRepository articleRepository, ReactionRepository reactionRepository) {
        this.articleRepository = articleRepository;
        this.reactionRepository = reactionRepository;
    }

    public List<Article> getAllArticle() {
        return articleRepository.findAll();
    }

    public Page<Article> getArticles(int page, String search, String userId) {
        Pageable pageable = PageRequest.of(page, 10);

        Page<Article> articlePage;


        if(search.trim().isEmpty()) {
            articlePage = articleRepository.findAll(pageable);
        } else {
            articlePage = articleRepository.findByArticleTitleContaining(search, pageable);
        }


        for(Article article : articlePage.getContent()) {
            Optional<Reaction> reaction = reactionRepository.findByArticleNumAndUserId(article.getArticleNum(), userId);

            if(reaction.isPresent()) {
                if(reaction.get().getReactionType().equals(ReactionType.LIKE)) {
                    article.setLike(true);
                } else {
                    article.setHate(true);
                }
            }
        }

        return articlePage;
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
    public void deleteArticle(ArticleData data) {
        articleRepository.deleteByArticleWriterAndArticleNum(data.getUserId(), data.getArticleNum());
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

    @Transactional
    public Article likeArticle(ArticleData data) {
        // DB에서 reaction 찾아옴
        Optional<Reaction> reaction = reactionRepository.findByArticleNumAndUserId(data.getArticleNum(), data.getUserId());
        Article article = articleRepository.findByArticleNum(data.getArticleNum());

        // 만약에 값이 있으면?
        if(reaction.isPresent()) {
            Reaction existingReaction = reaction.get();

            // 만약에 그 리액션이 좋아요였으면?
            if(existingReaction.getReactionType() == ReactionType.LIKE) {
                // 좋아요 토글(좋아요 해제)
                article.toggleLike();

                // 리액션 삭제
                reactionRepository.delete(existingReaction);
            } else {
                // 만약에 그 리액션이 싫어요였으면?
                // 싫어요 토글(싫어요 해제)
                article.toggleHate();

                // 좋아요 토글(좋아요 추가)
                article.toggleLike();

                // 리액션 타입 변경 [HATE -> LIKE]
                existingReaction.setReactionType(ReactionType.LIKE);
                reactionRepository.save(existingReaction);
            }
        // 만약에 값이 없으면??
        } else {
            // 새 좋아요 리액션 추가
            article.toggleLike();
            reactionRepository.save(new Reaction(data.getArticleNum(), data.getUserId(), ReactionType.LIKE));
        }

        // article like, hate 카운트 업데이트
        article.setLikeCount(reactionRepository.countByArticleNumAndReactionType(data.getArticleNum(), ReactionType.LIKE));
        article.setHateCount(reactionRepository.countByArticleNumAndReactionType(data.getArticleNum(), ReactionType.HATE));
        articleRepository.save(article);

        return article;
    }

    @Transactional
    public Article hateArticle(ArticleData data) {
        // DB에서 reaction 찾아옴
        Optional<Reaction> reaction = reactionRepository.findByArticleNumAndUserId(data.getArticleNum(), data.getUserId());
        Article article = articleRepository.findByArticleNum(data.getArticleNum());

        // 만약에 값이 있으면?
        if(reaction.isPresent()) {
            Reaction existingReaction = reaction.get();

            // 만약에 그 리액션이 싫어요였으면?
            if(existingReaction.getReactionType() == ReactionType.HATE) {
                // 싫어요 토글(싫어요 해제)
                article.toggleHate();

                // 리액션 삭제
                reactionRepository.delete(existingReaction);
            } else {
                // 만약에 그 리액션이 좋아요였으면?
                // 좋아요 토글(좋아요 해제)
                article.toggleLike();

                // 좋아요 토글(싫어요 추가)
                article.toggleHate();

                // 리액션 타입 변경 [LIKE -> HATE]
                existingReaction.setReactionType(ReactionType.HATE);
                reactionRepository.save(existingReaction);
            }
            // 만약에 값이 없으면??
        } else {
            // 새 좋아요 리액션 추가
            article.toggleHate();
            reactionRepository.save(new Reaction(data.getArticleNum(), data.getUserId(), ReactionType.HATE));
        }

        // article like, hate 카운트 업데이트
        article.setLikeCount(reactionRepository.countByArticleNumAndReactionType(data.getArticleNum(), ReactionType.LIKE));
        article.setHateCount(reactionRepository.countByArticleNumAndReactionType(data.getArticleNum(), ReactionType.HATE));
        articleRepository.save(article);

        return article;
    }

}
