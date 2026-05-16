package com.instagram.follow.repository;

import com.instagram.follow.entity.Follow;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends MongoRepository<Follow, String> {

    List<Follow> findByFollowerId(String followerId);

    List<Follow> findByFollowingId(String followingId);

    boolean existsByFollowerIdAndFollowingId(String followerId, String followingId);

    void deleteByFollowerIdAndFollowingId(String followerId, String followingId);

    long countByFollowerId(String followerId);

    long countByFollowingId(String followingId);
}
