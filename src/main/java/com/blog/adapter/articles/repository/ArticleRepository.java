package com.blog.adapter.articles.repository;

import com.blog.domain.articles.Article;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public interface ArticleRepository {
    Article save(Article article);

    Page<Article> getAllArticles(String orderBy, String sortBy, int page, int size);

    Article getArticleById(Long articleId);
}
