package com.instagram.follow.controller;

import com.instagram.follow.dto.FollowResponseDTO;
import com.instagram.follow.dto.FollowStatsDTO;
import com.instagram.follow.dto.UserSummaryDTO;
import com.instagram.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{targetUserId}")
    public ResponseEntity<FollowResponseDTO> followUser(
            @RequestHeader("X-User-Id") String followerId,
            @PathVariable String targetUserId) {
        FollowResponseDTO response = followService.followUser(followerId, targetUserId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{targetUserId}")
    public ResponseEntity<FollowResponseDTO> unfollowUser(
            @RequestHeader("X-User-Id") String followerId,
            @PathVariable String targetUserId) {
        FollowResponseDTO response = followService.unfollowUser(followerId, targetUserId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserSummaryDTO>> getFollowers(@PathVariable String userId) {
        List<UserSummaryDTO> followers = followService.getFollowers(userId);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<UserSummaryDTO>> getFollowing(@PathVariable String userId) {
        List<UserSummaryDTO> following = followService.getFollowing(userId);
        return ResponseEntity.ok(following);
    }

    @GetMapping("/{userId}/stats")
    public ResponseEntity<FollowStatsDTO> getFollowStats(@PathVariable String userId) {
        FollowStatsDTO stats = followService.getFollowStats(userId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/check/{targetUserId}")
    public ResponseEntity<Boolean> isFollowing(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String targetUserId) {
        boolean following = followService.isFollowing(userId, targetUserId);
        return ResponseEntity.ok(following);
    }
}
