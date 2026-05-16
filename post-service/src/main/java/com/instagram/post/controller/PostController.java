package com.instagram.post.controller;

import com.instagram.post.dto.CreatePostRequestDTO;
import com.instagram.post.dto.LikeResponseDTO;
import com.instagram.post.dto.PostResponseDTO;
import com.instagram.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreatePostRequestDTO request) {
        PostResponseDTO response = postService.createPost(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> getPostById(
            @PathVariable String postId,
            @RequestHeader("X-User-Id") String userId) {
        PostResponseDTO response = postService.getPostById(postId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostResponseDTO>> getPostsByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PostResponseDTO> response = postService.getPostsByUserId(userId, page, size);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable String postId,
            @RequestHeader("X-User-Id") String userId) {
        postService.deletePost(userId, postId);
        return ResponseEntity.noContent().build();
    }

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
}
