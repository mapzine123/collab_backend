package com.kgat.service;

import com.kgat.entity.Article;
import com.kgat.entity.ArticleReaction;
import com.kgat.repository.ArticleRepository;
import com.kgat.repository.ArticleReactionRepository;
import com.kgat.vo.ArticleData;
import com.kgat.vo.ReactionType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleReactionRepository articleReactionRepository;

    @Autowired
    public ArticleService(ArticleRepository articleRepository, ArticleReactionRepository articleReactionRepository) {
        this.articleRepository = articleRepository;
        this.articleReactionRepository = articleReactionRepository;
    }

    public List<Article> getAllArticle() {
        return articleRepository.findAll();
    }

    public Page<Article> getArticles(int page, String search, String userId) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Article> articlePage;

        // 검색어 유무에 따라 적절한 메서드 호출
        if(search == null || search.trim().isEmpty()) {
            articlePage = articleRepository.findAll(pageable);
        } else {
            articlePage = articleRepository.findByArticleTitleContaining(search.trim(), pageable);
        }

        if(userId != null && !articlePage.isEmpty()) {
            // 모든 Article ID 추출
            List<Long> articleIds = articlePage.getContent().stream()
                    .map(Article::getArticleId)
                    .collect(Collectors.toList());

            // 한번의 쿼리로 모든 관련 반응 조회
            List<ArticleReaction> reactions = articleReactionRepository.findByArticleIdInAndUserId(articleIds, userId);

            // 반응을 Map으로 변환
            Map<Long, ArticleReaction> reactionMap = reactions.stream()
                    .collect(Collectors.toMap(ArticleReaction::getArticleId, Function.identity()));

            // 각 Article에 반응 정보 설정
            articlePage.getContent().forEach(article -> {
               ArticleReaction reaction = reactionMap.get(article.getArticleId());
               if(reaction != null) {
                   if(ReactionType.LIKE.equals(reaction.getReactionType())) {
                       article.setLike(true);
                   } else {
                       article.setHate(true);
                   }
               }
            });
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
        articleRepository.deleteByArticleWriterAndArticleId(data.getUserId(), data.getArticleId());
    }

    public void updateArticle(Article article) {
        boolean isUpdated = false;

        try {
            Article prevArticle = articleRepository.findByArticleId(article.getArticleId());

            if(!article.getArticleTitle().equals(prevArticle.getArticleTitle())) {
                prevArticle.setArticleTitle(article.getArticleTitle());
                isUpdated = true;
            }

            if(!article.getArticleContent().equals(prevArticle.getArticleContent())) {
                prevArticle.setArticleContent(article.getArticleContent());
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
        Optional<ArticleReaction> reaction = articleReactionRepository.findByArticleIdAndUserId(data.getArticleId(), data.getUserId());
        Article article = articleRepository.findByArticleId(data.getArticleId());

        // 만약에 값이 있으면?
        if(reaction.isPresent()) {
            ArticleReaction existingArticleReaction = reaction.get();

            // 만약에 그 리액션이 좋아요였으면?
            if(existingArticleReaction.getReactionType() == ReactionType.LIKE) {
                // 좋아요 토글(좋아요 해제)
                article.toggleLike();

                // 리액션 삭제
                articleReactionRepository.delete(existingArticleReaction);
            } else {
                // 만약에 그 리액션이 싫어요였으면?
                // 싫어요 토글(싫어요 해제)
                article.toggleHate();

                // 좋아요 토글(좋아요 추가)
                article.toggleLike();

                // 리액션 타입 변경 [HATE -> LIKE]
                existingArticleReaction.setReactionType(ReactionType.LIKE);
                articleReactionRepository.save(existingArticleReaction);
            }
        // 만약에 값이 없으면??
        } else {
            // 새 좋아요 리액션 추가
            article.toggleLike();
            articleReactionRepository.save(new ArticleReaction(data.getArticleId(), data.getUserId(), ReactionType.LIKE));
        }

        // article like, hate 카운트 업데이트
        article.setLikeCount(articleReactionRepository.countByArticleIdAndReactionType(data.getArticleId(), ReactionType.LIKE));
        article.setHateCount(articleReactionRepository.countByArticleIdAndReactionType(data.getArticleId(), ReactionType.HATE));
        articleRepository.save(article);

        return article;
    }

    @Transactional
    public Article hateArticle(ArticleData data) {
        // DB에서 reaction 찾아옴
        Optional<ArticleReaction> reaction = articleReactionRepository.findByArticleIdAndUserId(data.getArticleId(), data.getUserId());
        Article article = articleRepository.findByArticleId(data.getArticleId());

        // 만약에 값이 있으면?
        if(reaction.isPresent()) {
            ArticleReaction existingArticleReaction = reaction.get();

            // 만약에 그 리액션이 싫어요였으면?
            if(existingArticleReaction.getReactionType() == ReactionType.HATE) {
                // 싫어요 토글(싫어요 해제)
                article.toggleHate();

                // 리액션 삭제
                articleReactionRepository.delete(existingArticleReaction);
            } else {
                // 만약에 그 리액션이 좋아요였으면?
                // 좋아요 토글(좋아요 해제)
                article.toggleLike();

                // 좋아요 토글(싫어요 추가)
                article.toggleHate();

                // 리액션 타입 변경 [LIKE -> HATE]
                existingArticleReaction.setReactionType(ReactionType.HATE);
                articleReactionRepository.save(existingArticleReaction);
            }
            // 만약에 값이 없으면??
        } else {
            // 새 좋아요 리액션 추가
            article.toggleHate();
            articleReactionRepository.save(new ArticleReaction(data.getArticleId(), data.getUserId(), ReactionType.HATE));
        }

        // article like, hate 카운트 업데이트
        article.setLikeCount(articleReactionRepository.countByArticleIdAndReactionType(data.getArticleId(), ReactionType.LIKE));
        article.setHateCount(articleReactionRepository.countByArticleIdAndReactionType(data.getArticleId(), ReactionType.HATE));
        articleRepository.save(article);

        return article;
    }

}
