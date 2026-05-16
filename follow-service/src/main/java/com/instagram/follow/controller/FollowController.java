package com.instagram.follow.controller;

import com.instagram.follow.dto.FollowResponseDTO;
import com.instagram.follow.dto.FollowStatsDTO;
import com.instagram.follow.dto.UserSummaryDTO;
import com.instagram.follow.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
@Tag(name = "Follow", description = "Follow/unfollow users, follower management")
public class FollowController {

    private final FollowService followService;

    @Operation(summary = "Follow a user", description = "Creates a follow relationship from the authenticated user to the target user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User followed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid follow request (e.g., self-follow)"),
        @ApiResponse(responseCode = "409", description = "Already following this user")
    })
    @PostMapping("/{targetUserId}")
    public ResponseEntity<FollowResponseDTO> followUser(
            @RequestHeader("X-User-Id") String followerId,
            @PathVariable String targetUserId) {
        FollowResponseDTO response = followService.followUser(followerId, targetUserId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Unfollow a user", description = "Removes the follow relationship from the authenticated user to the target user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User unfollowed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid unfollow request")
    })
    @DeleteMapping("/{targetUserId}")
    public ResponseEntity<FollowResponseDTO> unfollowUser(
            @RequestHeader("X-User-Id") String followerId,
            @PathVariable String targetUserId) {
        FollowResponseDTO response = followService.unfollowUser(followerId, targetUserId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get followers", description = "Retrieves the list of users who follow the specified user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Followers list retrieved successfully")
    })
    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserSummaryDTO>> getFollowers(@PathVariable String userId) {
        List<UserSummaryDTO> followers = followService.getFollowers(userId);
        return ResponseEntity.ok(followers);
    }

    @Operation(summary = "Get following", description = "Retrieves the list of users that the specified user is following")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Following list retrieved successfully")
    })
    @GetMapping("/{userId}/following")
    public ResponseEntity<List<UserSummaryDTO>> getFollowing(@PathVariable String userId) {
        List<UserSummaryDTO> following = followService.getFollowing(userId);
        return ResponseEntity.ok(following);
    }

    @Operation(summary = "Get follow stats", description = "Retrieves follower and following counts for the specified user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Follow stats retrieved successfully")
    })
    @GetMapping("/{userId}/stats")
    public ResponseEntity<FollowStatsDTO> getFollowStats(@PathVariable String userId) {
        FollowStatsDTO stats = followService.getFollowStats(userId);
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Check following status", description = "Checks if the authenticated user is following the target user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Following status retrieved successfully")
    })
    @GetMapping("/check/{targetUserId}")
    public ResponseEntity<Boolean> isFollowing(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String targetUserId) {
        boolean following = followService.isFollowing(userId, targetUserId);
        return ResponseEntity.ok(following);
    }

    @Operation(summary = "Get following IDs", description = "Returns a list of user IDs that the specified user is following")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Following IDs retrieved successfully")
    })
    @GetMapping("/{userId}/following/ids")
    public ResponseEntity<List<String>> getFollowingIds(@PathVariable String userId) {
        List<String> ids = followService.getFollowingIds(userId);
        return ResponseEntity.ok(ids);
    }
}
