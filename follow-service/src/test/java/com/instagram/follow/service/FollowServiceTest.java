package com.instagram.follow.service;

import com.instagram.follow.dto.FollowResponseDTO;
import com.instagram.follow.dto.FollowStatsDTO;
import com.instagram.follow.dto.UserSummaryDTO;
import com.instagram.follow.entity.Follow;
import com.instagram.follow.exception.AlreadyFollowingException;
import com.instagram.follow.exception.NotFollowingException;
import com.instagram.follow.exception.SelfFollowException;
import com.instagram.follow.client.AuthServiceClient;
import com.instagram.follow.repository.FollowRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private AuthServiceClient authServiceClient;

    @InjectMocks
    private FollowService followService;

    private static final String FOLLOWER_ID = "user-123";
    private static final String FOLLOWING_ID = "user-456";

    // ===== followUser tests =====

    @Test
    void followUser_validUsers_createsFollow() {
        when(followRepository.existsByFollowerIdAndFollowingId(FOLLOWER_ID, FOLLOWING_ID)).thenReturn(false);
        when(followRepository.save(any(Follow.class))).thenReturn(Follow.builder()
                .id("follow-1")
                .followerId(FOLLOWER_ID)
                .followingId(FOLLOWING_ID)
                .createdAt(LocalDateTime.now())
                .build());

        FollowResponseDTO result = followService.followUser(FOLLOWER_ID, FOLLOWING_ID);

        assertNotNull(result);
        assertEquals(FOLLOWER_ID, result.getFollowerId());
        assertEquals(FOLLOWING_ID, result.getFollowingId());
        assertTrue(result.isFollowing());
        assertEquals("Successfully followed user", result.getMessage());

        ArgumentCaptor<Follow> followCaptor = ArgumentCaptor.forClass(Follow.class);
        verify(followRepository).save(followCaptor.capture());
        assertEquals(FOLLOWER_ID, followCaptor.getValue().getFollowerId());
        assertEquals(FOLLOWING_ID, followCaptor.getValue().getFollowingId());
    }

    @Test
    void followUser_selfFollow_throwsSelfFollowException() {
        assertThrows(SelfFollowException.class,
                () -> followService.followUser(FOLLOWER_ID, FOLLOWER_ID));

        verify(followRepository, never()).save(any(Follow.class));
    }

    @Test
    void followUser_alreadyFollowing_throwsAlreadyFollowingException() {
        when(followRepository.existsByFollowerIdAndFollowingId(FOLLOWER_ID, FOLLOWING_ID)).thenReturn(true);

        assertThrows(AlreadyFollowingException.class,
                () -> followService.followUser(FOLLOWER_ID, FOLLOWING_ID));

        verify(followRepository, never()).save(any(Follow.class));
    }

    // ===== unfollowUser tests =====

    @Test
    void unfollowUser_whenFollowing_deletesFollow() {
        when(followRepository.existsByFollowerIdAndFollowingId(FOLLOWER_ID, FOLLOWING_ID)).thenReturn(true);

        FollowResponseDTO result = followService.unfollowUser(FOLLOWER_ID, FOLLOWING_ID);

        assertNotNull(result);
        assertEquals(FOLLOWER_ID, result.getFollowerId());
        assertEquals(FOLLOWING_ID, result.getFollowingId());
        assertFalse(result.isFollowing());
        assertEquals("Successfully unfollowed user", result.getMessage());

        verify(followRepository).deleteByFollowerIdAndFollowingId(FOLLOWER_ID, FOLLOWING_ID);
    }

    @Test
    void unfollowUser_whenNotFollowing_throwsNotFollowingException() {
        when(followRepository.existsByFollowerIdAndFollowingId(FOLLOWER_ID, FOLLOWING_ID)).thenReturn(false);

        assertThrows(NotFollowingException.class,
                () -> followService.unfollowUser(FOLLOWER_ID, FOLLOWING_ID));

        verify(followRepository, never()).deleteByFollowerIdAndFollowingId(anyString(), anyString());
    }

    // ===== getFollowers tests =====

    @Test
    void getFollowers_returnsUserSummaryList() {
        List<Follow> followers = List.of(
                Follow.builder().id("f1").followerId("user-A").followingId(FOLLOWING_ID).build(),
                Follow.builder().id("f2").followerId("user-B").followingId(FOLLOWING_ID).build()
        );
        when(followRepository.findByFollowingId(FOLLOWING_ID)).thenReturn(followers);
        when(authServiceClient.getUserSummary("user-A")).thenReturn(
                UserSummaryDTO.builder().id("user-A").username("userA").build());
        when(authServiceClient.getUserSummary("user-B")).thenReturn(
                UserSummaryDTO.builder().id("user-B").username("userB").build());

        List<UserSummaryDTO> result = followService.getFollowers(FOLLOWING_ID);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user-A", result.get(0).getId());
        assertEquals("user-B", result.get(1).getId());
        verify(followRepository).findByFollowingId(FOLLOWING_ID);
    }

    // ===== getFollowing tests =====

    @Test
    void getFollowing_returnsUserSummaryList() {
        List<Follow> following = List.of(
                Follow.builder().id("f1").followerId(FOLLOWER_ID).followingId("user-X").build(),
                Follow.builder().id("f2").followerId(FOLLOWER_ID).followingId("user-Y").build()
        );
        when(followRepository.findByFollowerId(FOLLOWER_ID)).thenReturn(following);
        when(authServiceClient.getUserSummary("user-X")).thenReturn(
                UserSummaryDTO.builder().id("user-X").username("userX").build());
        when(authServiceClient.getUserSummary("user-Y")).thenReturn(
                UserSummaryDTO.builder().id("user-Y").username("userY").build());

        List<UserSummaryDTO> result = followService.getFollowing(FOLLOWER_ID);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user-X", result.get(0).getId());
        assertEquals("user-Y", result.get(1).getId());
        verify(followRepository).findByFollowerId(FOLLOWER_ID);
    }

    // ===== getFollowStats tests =====

    @Test
    void getFollowStats_returnsCorrectCounts() {
        when(followRepository.countByFollowingId(FOLLOWER_ID)).thenReturn(100L);
        when(followRepository.countByFollowerId(FOLLOWER_ID)).thenReturn(50L);

        FollowStatsDTO result = followService.getFollowStats(FOLLOWER_ID);

        assertNotNull(result);
        assertEquals(FOLLOWER_ID, result.getUserId());
        assertEquals(100, result.getFollowerCount());
        assertEquals(50, result.getFollowingCount());
    }

    // ===== isFollowing tests =====

    @Test
    void isFollowing_whenFollowing_returnsTrue() {
        when(followRepository.existsByFollowerIdAndFollowingId(FOLLOWER_ID, FOLLOWING_ID)).thenReturn(true);

        boolean result = followService.isFollowing(FOLLOWER_ID, FOLLOWING_ID);

        assertTrue(result);
    }

    @Test
    void isFollowing_whenNotFollowing_returnsFalse() {
        when(followRepository.existsByFollowerIdAndFollowingId(FOLLOWER_ID, FOLLOWING_ID)).thenReturn(false);

        boolean result = followService.isFollowing(FOLLOWER_ID, FOLLOWING_ID);

        assertFalse(result);
    }
}
