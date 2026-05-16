package com.instagram.trending.controller;

import com.instagram.trending.dto.TrendingHashtagDTO;
import com.instagram.trending.dto.TrendingPostDTO;
import com.instagram.trending.service.TrendingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Trending", description = "Trending posts and hashtags")
public class TrendingController {

    private final TrendingService trendingService;

    @Operation(summary = "Get trending posts", description = "Retrieves trending posts with filtering and pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trending posts retrieved successfully")
    })
    @GetMapping("/posts")
    public ResponseEntity<Page<TrendingPostDTO>> getTrendingPosts(
            @RequestParam(defaultValue = "popular") String filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/trending/posts?filter={}&page={}&size={}", filter, page, size);
        Page<TrendingPostDTO> posts = trendingService.getTrendingPosts(filter, page, size);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "Get trending hashtags", description = "Retrieves the top trending hashtags")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trending hashtags retrieved successfully")
    })
    @GetMapping("/hashtags")
    public ResponseEntity<List<TrendingHashtagDTO>> getTrendingHashtags(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("GET /api/trending/hashtags?limit={}", limit);
        List<TrendingHashtagDTO> hashtags = trendingService.getTrendingHashtags(limit);
        return ResponseEntity.ok(hashtags);
    }

    @Operation(summary = "Add a trending post", description = "Adds a post to the trending posts collection")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Post added to trending successfully")
    })
    @PostMapping("/posts")
    public ResponseEntity<TrendingPostDTO> addPost(@RequestBody TrendingPostDTO dto) {
        log.info("POST /api/trending/posts - postId: {}", dto.getPostId());
        TrendingPostDTO created = trendingService.addPost(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Remove a trending post", description = "Removes a post from the trending posts collection")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Post removed from trending successfully")
    })
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> removePost(@PathVariable String postId) {
        log.info("DELETE /api/trending/posts/{}", postId);
        trendingService.removePost(postId);
        return ResponseEntity.noContent().build();
    }
}
