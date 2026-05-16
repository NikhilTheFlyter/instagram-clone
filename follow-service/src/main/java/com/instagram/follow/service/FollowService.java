package com.instagram.follow.service;

import com.instagram.follow.dto.FollowResponseDTO;
import com.instagram.follow.dto.FollowStatsDTO;
import com.instagram.follow.dto.UserSummaryDTO;
import com.instagram.follow.entity.Follow;
import com.instagram.follow.exception.AlreadyFollowingException;
import com.instagram.follow.exception.NotFollowingException;
import com.instagram.follow.exception.SelfFollowException;
import com.instagram.follow.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService {

    private final FollowRepository followRepository;

    public FollowResponseDTO followUser(String followerId, String followingId) {
        if (followerId.equals(followingId)) {
            throw new SelfFollowException("You cannot follow yourself");
        }

        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new AlreadyFollowingException("You are already following this user");
        }

        Follow follow = Follow.builder()
                .followerId(followerId)
                .followingId(followingId)
                .build();

        followRepository.save(follow);

        return FollowResponseDTO.builder()
                .followerId(followerId)
                .followingId(followingId)
                .isFollowing(true)
                .message("Successfully followed user")
                .build();
    }

    public FollowResponseDTO unfollowUser(String followerId, String followingId) {
        if (!followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new NotFollowingException("You are not following this user");
        }

        followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);

        return FollowResponseDTO.builder()
                .followerId(followerId)
                .followingId(followingId)
                .isFollowing(false)
                .message("Successfully unfollowed user")
                .build();
    }

    public List<UserSummaryDTO> getFollowers(String userId) {
        List<Follow> followers = followRepository.findByFollowingId(userId);
        return followers.stream()
                .map(follow -> UserSummaryDTO.builder()
                        .id(follow.getFollowerId())
                        .build())
                .collect(Collectors.toList());
    }

    public List<UserSummaryDTO> getFollowing(String userId) {
        List<Follow> following = followRepository.findByFollowerId(userId);
        return following.stream()
                .map(follow -> UserSummaryDTO.builder()
                        .id(follow.getFollowingId())
                        .build())
                .collect(Collectors.toList());
    }

    public FollowStatsDTO getFollowStats(String userId) {
        long followerCount = followRepository.countByFollowingId(userId);
        long followingCount = followRepository.countByFollowerId(userId);

        return FollowStatsDTO.builder()
                .userId(userId)
                .followerCount(followerCount)
                .followingCount(followingCount)
                .build();
    }

    public boolean isFollowing(String followerId, String followingId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }
}
