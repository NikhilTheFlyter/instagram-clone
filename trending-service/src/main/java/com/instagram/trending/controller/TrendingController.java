package com.instagram.trending.controller;

import com.instagram.trending.dto.TrendingHashtagDTO;
import com.instagram.trending.dto.TrendingPostDTO;
import com.instagram.trending.service.TrendingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/trending")
@RequiredArgsConstructor
@Slf4j
public class TrendingController {

    private final TrendingService trendingService;

    @GetMapping("/posts")
    public ResponseEntity<Page<TrendingPostDTO>> getTrendingPosts(
            @RequestParam(defaultValue = "popular") String filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/trending/posts?filter={}&page={}&size={}", filter, page, size);
        Page<TrendingPostDTO> posts = trendingService.getTrendingPosts(filter, page, size);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/hashtags")
    public ResponseEntity<List<TrendingHashtagDTO>> getTrendingHashtags(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("GET /api/trending/hashtags?limit={}", limit);
        List<TrendingHashtagDTO> hashtags = trendingService.getTrendingHashtags(limit);
        return ResponseEntity.ok(hashtags);
    }

    @PostMapping("/posts")
    public ResponseEntity<TrendingPostDTO> addPost(@RequestBody TrendingPostDTO dto) {
        log.info("POST /api/trending/posts - postId: {}", dto.getPostId());
        TrendingPostDTO created = trendingService.addPost(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> removePost(@PathVariable String postId) {
        log.info("DELETE /api/trending/posts/{}", postId);
        trendingService.removePost(postId);
        return ResponseEntity.noContent().build();
    }
}
