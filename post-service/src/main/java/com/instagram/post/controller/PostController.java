package com.instagram.post.controller;

import com.instagram.post.dto.CreatePostRequestDTO;
import com.instagram.post.dto.LikeResponseDTO;
import com.instagram.post.dto.PostResponseDTO;
import com.instagram.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Posts", description = "Post management - create, view, like, delete")
public class PostController {

    private final PostService postService;

    @Operation(summary = "Create a new post", description = "Creates a new post for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Post created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid post data")
    })
    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreatePostRequestDTO request) {
        PostResponseDTO response = postService.createPost(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get post by ID", description = "Retrieves a specific post by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Post retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> getPostById(
            @PathVariable String postId,
            @RequestHeader("X-User-Id") String userId) {
        PostResponseDTO response = postService.getPostById(postId, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get posts by user", description = "Retrieves all posts created by a specific user with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostResponseDTO>> getPostsByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PostResponseDTO> response = postService.getPostsByUserId(userId, page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a post", description = "Deletes a post owned by the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Post deleted successfully"),
        @ApiResponse(responseCode = "403", description = "User not authorized to delete this post"),
        @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable String postId,
            @RequestHeader("X-User-Id") String userId) {
        postService.deletePost(userId, postId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Like a post", description = "Adds a like to the specified post by the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Post liked successfully"),
        @ApiResponse(responseCode = "409", description = "User has already liked this post")
    })
    @PostMapping("/{postId}/like")
    public ResponseEntity<LikeResponseDTO> likePost(
            @PathVariable String postId,
            @RequestHeader("X-User-Id") String userId) {
        LikeResponseDTO response = postService.likePost(userId, postId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<LikeResponseDTO> unlikePost(
            @PathVariable String postId,
            @RequestHeader("X-User-Id") String userId) {
        LikeResponseDTO response = postService.unlikePost(userId, postId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}/likes")
    public ResponseEntity<LikeResponseDTO> getLikeStatus(
            @PathVariable String postId,
            @RequestHeader("X-User-Id") String userId) {
        LikeResponseDTO response = postService.getLikeStatus(userId, postId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PostResponseDTO>> searchPosts(
            @RequestParam String q,
            @RequestParam(defaultValue = "relevance") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Search posts request: q={}, sort={}", q, sort);
        Page<PostResponseDTO> results = postService.searchPosts(q, sort, page, size);
        return ResponseEntity.ok(results);
    }
}
