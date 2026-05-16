package com.instagram.trending.repository;

import com.instagram.trending.entity.TrendingPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TrendingPostRepository extends MongoRepository<TrendingPost, String> {

    Page<TrendingPost> findAllByOrderByScoreDesc(Pageable pageable);

    Page<TrendingPost> findByHashtagsContaining(String hashtag, Pageable pageable);

    Page<TrendingPost> findAllByOrderByCalculatedAtDesc(Pageable pageable);

    Optional<TrendingPost> findByPostId(String postId);

    void deleteByCalculatedAtBefore(LocalDateTime cutoff);
}
