package com.instagram.post.repository;

import com.instagram.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {

    Page<Post> findByUserId(String userId, Pageable pageable);

    Page<Post> findByUserIdIn(List<String> userIds, Pageable pageable);

    Page<Post> findByHashtagsContaining(String hashtag, Pageable pageable);
}
