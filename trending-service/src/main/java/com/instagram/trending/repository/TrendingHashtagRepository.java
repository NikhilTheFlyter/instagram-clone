package com.instagram.trending.repository;

import com.instagram.trending.entity.TrendingHashtag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrendingHashtagRepository extends MongoRepository<TrendingHashtag, String> {

    Page<TrendingHashtag> findAllByOrderByScoreDesc(Pageable pageable);

    Optional<TrendingHashtag> findByHashtag(String hashtag);
}
