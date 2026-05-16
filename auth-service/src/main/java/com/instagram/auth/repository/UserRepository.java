package com.instagram.auth.repository;

import com.instagram.auth.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("{'$or': [{'username': {'$regex': ?0, '$options': 'i'}}, {'fullName': {'$regex': ?0, '$options': 'i'}}, {'bio': {'$regex': ?0, '$options': 'i'}}]}")
    Page<User> searchUsers(String keyword, Pageable pageable);
}
