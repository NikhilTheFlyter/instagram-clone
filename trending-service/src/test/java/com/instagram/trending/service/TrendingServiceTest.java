package com.instagram.trending.service;

import com.instagram.trending.dto.TrendingHashtagDTO;
import com.instagram.trending.dto.TrendingPostDTO;
import com.instagram.trending.entity.TrendingHashtag;
import com.instagram.trending.entity.TrendingPost;
import com.instagram.trending.repository.TrendingHashtagRepository;
import com.instagram.trending.repository.TrendingPostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrendingServiceTest {

    @Mock
    private TrendingPostRepository trendingPostRepository;

    @Mock
    private TrendingHashtagRepository trendingHashtagRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TrendingService trendingService;

    private TrendingPost samplePost;
    private TrendingPostDTO samplePostDTO;
    private TrendingHashtag sampleHashtag;
    private TrendingHashtagDTO sampleHashtagDTO;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        samplePost = TrendingPost.builder()
                .id("t1")
                .postId("post-1")
                .userId("user-1")
                .caption("Test caption")
                .mediaUrls(List.of("http://img.com/1.jpg"))
                .hashtags(List.of("#test"))
                .likesCount(50)
                .score(200.0)
                .calculatedAt(now)
                .build();

        samplePostDTO = TrendingPostDTO.builder()
                .id("t1")
                .postId("post-1")
                .userId("user-1")
                .caption("Test caption")
                .mediaUrls(List.of("http://img.com/1.jpg"))
                .hashtags(List.of("#test"))
                .likesCount(50)
                .score(200.0)
                .calculatedAt(now)
                .build();

        sampleHashtag = TrendingHashtag.builder()
                .id("h1")
                .hashtag("#test")
                .postCount(10)
                .score(100.0)
                .calculatedAt(now)
                .build();

        sampleHashtagDTO = TrendingHashtagDTO.builder()
                .hashtag("#test")
                .postCount(10)
                .score(100.0)
                .build();
    }

    // ---- getTrendingPosts tests ----

    @Test
    void getTrendingPosts_popularFilter_returnsByScore() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TrendingPost> postPage = new PageImpl<>(List.of(samplePost), pageable, 1);

        when(trendingPostRepository.findAllByOrderByScoreDesc(pageable)).thenReturn(postPage);
        when(modelMapper.map(samplePost, TrendingPostDTO.class)).thenReturn(samplePostDTO);

        Page<TrendingPostDTO> result = trendingService.getTrendingPosts("popular", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(samplePostDTO, result.getContent().get(0));
        verify(trendingPostRepository).findAllByOrderByScoreDesc(pageable);
        verify(trendingPostRepository, never()).findAllByOrderByCalculatedAtDesc(any());
    }

    @Test
    void getTrendingPosts_recentFilter_returnsByDate() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TrendingPost> postPage = new PageImpl<>(List.of(samplePost), pageable, 1);

        when(trendingPostRepository.findAllByOrderByCalculatedAtDesc(pageable)).thenReturn(postPage);
        when(modelMapper.map(samplePost, TrendingPostDTO.class)).thenReturn(samplePostDTO);

        Page<TrendingPostDTO> result = trendingService.getTrendingPosts("recent", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(samplePostDTO, result.getContent().get(0));
        verify(trendingPostRepository).findAllByOrderByCalculatedAtDesc(pageable);
        verify(trendingPostRepository, never()).findAllByOrderByScoreDesc(any());
    }

    @Test
    void getTrendingPosts_defaultFilter_returnsByScore() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TrendingPost> postPage = new PageImpl<>(List.of(samplePost), pageable, 1);

        when(trendingPostRepository.findAllByOrderByScoreDesc(pageable)).thenReturn(postPage);
        when(modelMapper.map(samplePost, TrendingPostDTO.class)).thenReturn(samplePostDTO);

        // Pass null as filter -- should default to score ordering
        Page<TrendingPostDTO> result = trendingService.getTrendingPosts(null, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(trendingPostRepository).findAllByOrderByScoreDesc(pageable);
        verify(trendingPostRepository, never()).findAllByOrderByCalculatedAtDesc(any());
    }

    // ---- getTrendingHashtags tests ----

    @Test
    void getTrendingHashtags_returnsTopHashtags() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<TrendingHashtag> hashtagPage = new PageImpl<>(List.of(sampleHashtag), pageable, 1);

        when(trendingHashtagRepository.findAllByOrderByScoreDesc(pageable)).thenReturn(hashtagPage);
        when(modelMapper.map(sampleHashtag, TrendingHashtagDTO.class)).thenReturn(sampleHashtagDTO);

        List<TrendingHashtagDTO> result = trendingService.getTrendingHashtags(5);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sampleHashtagDTO, result.get(0));
        verify(trendingHashtagRepository).findAllByOrderByScoreDesc(pageable);
    }

    // ---- addPost tests ----

    @Test
    void addPost_newPost_createsTrendingPost() {
        TrendingPostDTO inputDTO = TrendingPostDTO.builder()
                .postId("post-new")
                .userId("user-1")
                .caption("New post")
                .mediaUrls(List.of("http://img.com/new.jpg"))
                .hashtags(List.of("#new"))
                .likesCount(10)
                .build();

        TrendingPost mappedPost = TrendingPost.builder()
                .postId("post-new")
                .userId("user-1")
                .caption("New post")
                .mediaUrls(List.of("http://img.com/new.jpg"))
                .hashtags(List.of("#new"))
                .likesCount(10)
                .build();

        TrendingPost savedPost = TrendingPost.builder()
                .id("generated-id")
                .postId("post-new")
                .userId("user-1")
                .caption("New post")
                .mediaUrls(List.of("http://img.com/new.jpg"))
                .hashtags(List.of("#new"))
                .likesCount(10)
                .score(120.0)  // 10 * 2.0 + 100.0 recency bonus
                .calculatedAt(LocalDateTime.now())
                .build();

        TrendingPostDTO savedDTO = TrendingPostDTO.builder()
                .id("generated-id")
                .postId("post-new")
                .userId("user-1")
                .caption("New post")
                .mediaUrls(List.of("http://img.com/new.jpg"))
                .hashtags(List.of("#new"))
                .likesCount(10)
                .score(120.0)
                .build();

        when(trendingPostRepository.findByPostId("post-new")).thenReturn(Optional.empty());
        when(modelMapper.map(inputDTO, TrendingPost.class)).thenReturn(mappedPost);
        when(trendingPostRepository.save(any(TrendingPost.class))).thenReturn(savedPost);
        when(modelMapper.map(savedPost, TrendingPostDTO.class)).thenReturn(savedDTO);

        TrendingPostDTO result = trendingService.addPost(inputDTO);

        assertNotNull(result);
        assertEquals("post-new", result.getPostId());
        assertEquals("generated-id", result.getId());
        verify(trendingPostRepository).findByPostId("post-new");
        verify(modelMapper).map(inputDTO, TrendingPost.class);
        verify(trendingPostRepository).save(any(TrendingPost.class));
    }

    @Test
    void addPost_existingPost_updatesScore() {
        TrendingPost existingPost = TrendingPost.builder()
                .id("existing-id")
                .postId("post-1")
                .userId("user-1")
                .caption("Old caption")
                .mediaUrls(List.of("http://img.com/old.jpg"))
                .hashtags(List.of("#old"))
                .likesCount(20)
                .score(140.0)
                .calculatedAt(LocalDateTime.now().minusHours(2))
                .build();

        TrendingPostDTO updateDTO = TrendingPostDTO.builder()
                .postId("post-1")
                .userId("user-1")
                .caption("Updated caption")
                .mediaUrls(List.of("http://img.com/updated.jpg"))
                .hashtags(List.of("#updated"))
                .likesCount(100)
                .build();

        TrendingPost savedPost = TrendingPost.builder()
                .id("existing-id")
                .postId("post-1")
                .userId("user-1")
                .caption("Updated caption")
                .mediaUrls(List.of("http://img.com/updated.jpg"))
                .hashtags(List.of("#updated"))
                .likesCount(100)
                .score(300.0)  // 100 * 2.0 + 100.0
                .calculatedAt(LocalDateTime.now())
                .build();

        TrendingPostDTO savedDTO = TrendingPostDTO.builder()
                .id("existing-id")
                .postId("post-1")
                .userId("user-1")
                .caption("Updated caption")
                .mediaUrls(List.of("http://img.com/updated.jpg"))
                .hashtags(List.of("#updated"))
                .likesCount(100)
                .score(300.0)
                .build();

        when(trendingPostRepository.findByPostId("post-1")).thenReturn(Optional.of(existingPost));
        when(trendingPostRepository.save(any(TrendingPost.class))).thenReturn(savedPost);
        when(modelMapper.map(savedPost, TrendingPostDTO.class)).thenReturn(savedDTO);

        TrendingPostDTO result = trendingService.addPost(updateDTO);

        assertNotNull(result);
        assertEquals("existing-id", result.getId());
        assertEquals("Updated caption", result.getCaption());
        assertEquals(100, result.getLikesCount());
        verify(trendingPostRepository).findByPostId("post-1");
        // Should NOT map DTO to entity for existing post (it updates fields directly)
        verify(modelMapper, never()).map(updateDTO, TrendingPost.class);
        verify(trendingPostRepository).save(any(TrendingPost.class));
    }

    // ---- removePost tests ----

    @Test
    void removePost_existingPost_deletes() {
        when(trendingPostRepository.findByPostId("post-1")).thenReturn(Optional.of(samplePost));

        trendingService.removePost("post-1");

        verify(trendingPostRepository).findByPostId("post-1");
        verify(trendingPostRepository).delete(samplePost);
    }

    @Test
    void removePost_nonExistent_noAction() {
        when(trendingPostRepository.findByPostId("non-existent")).thenReturn(Optional.empty());

        trendingService.removePost("non-existent");

        verify(trendingPostRepository).findByPostId("non-existent");
        verify(trendingPostRepository, never()).delete(any(TrendingPost.class));
    }
}
