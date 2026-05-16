package com.instagram.post.service;

import com.instagram.post.dto.CreatePostRequestDTO;
import com.instagram.post.dto.LikeResponseDTO;
import com.instagram.post.dto.PostResponseDTO;
import com.instagram.post.entity.Like;
import com.instagram.post.entity.MediaType;
import com.instagram.post.entity.Post;
import com.instagram.post.entity.Privacy;
import com.instagram.post.exception.AlreadyLikedException;
import com.instagram.post.exception.PostNotFoundException;
import com.instagram.post.exception.UnauthorizedAccessException;
import com.instagram.post.client.FollowServiceClient;
import com.instagram.post.client.TrendingServiceClient;
import com.instagram.post.repository.LikeRepository;
import com.instagram.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private FollowServiceClient followServiceClient;

    @Mock
    private TrendingServiceClient trendingServiceClient;

    @InjectMocks
    private PostService postService;

    private Post samplePost;
    private PostResponseDTO samplePostResponseDTO;
    private CreatePostRequestDTO sampleCreatePostRequestDTO;

    private static final String USER_ID = "user-123";
    private static final String POST_ID = "post-456";
    private static final String OTHER_USER_ID = "user-789";

    @BeforeEach
    void setUp() {
        samplePost = Post.builder()
                .id(POST_ID)
                .userId(USER_ID)
                .caption("Test caption")
                .mediaUrls(List.of("https://example.com/image.jpg"))
                .mediaType(MediaType.IMAGE)
                .hashtags(List.of("test", "unit"))
                .tags(List.of("friend1"))
                .privacy(Privacy.PUBLIC)
                .likesCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        samplePostResponseDTO = PostResponseDTO.builder()
                .id(POST_ID)
                .userId(USER_ID)
                .caption("Test caption")
                .mediaUrls(List.of("https://example.com/image.jpg"))
                .mediaType(MediaType.IMAGE)
                .hashtags(List.of("test", "unit"))
                .tags(List.of("friend1"))
                .privacy(Privacy.PUBLIC)
                .likesCount(0)
                .liked(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleCreatePostRequestDTO = CreatePostRequestDTO.builder()
                .caption("Test caption")
                .mediaUrls(List.of("https://example.com/image.jpg"))
                .mediaType(MediaType.IMAGE)
                .hashtags(List.of("test", "unit"))
                .tags(List.of("friend1"))
                .privacy(Privacy.PUBLIC)
                .build();
    }

    // ===== createPost tests =====

    @Test
    void createPost_withValidData_returnsPostResponseDTO() {
        when(postRepository.save(any(Post.class))).thenReturn(samplePost);
        when(modelMapper.map(any(Post.class), eq(PostResponseDTO.class))).thenReturn(samplePostResponseDTO);

        PostResponseDTO result = postService.createPost(USER_ID, sampleCreatePostRequestDTO);

        assertNotNull(result);
        assertEquals(POST_ID, result.getId());
        assertEquals(USER_ID, result.getUserId());
        assertEquals("Test caption", result.getCaption());
        assertFalse(result.isLiked());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void createPost_setsUserIdAndDefaults() {
        when(postRepository.save(any(Post.class))).thenReturn(samplePost);
        when(modelMapper.map(any(Post.class), eq(PostResponseDTO.class))).thenReturn(samplePostResponseDTO);

        postService.createPost(USER_ID, sampleCreatePostRequestDTO);

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());

        Post capturedPost = postCaptor.getValue();
        assertEquals(USER_ID, capturedPost.getUserId());
        assertEquals(0, capturedPost.getLikesCount());
        assertEquals("Test caption", capturedPost.getCaption());
        assertEquals(MediaType.IMAGE, capturedPost.getMediaType());
        assertEquals(Privacy.PUBLIC, capturedPost.getPrivacy());
    }

    // ===== getPostById tests =====

    @Test
    void getPostById_existingPost_returnsDTO() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(samplePost));
        when(likeRepository.existsByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(false);
        when(modelMapper.map(any(Post.class), eq(PostResponseDTO.class))).thenReturn(samplePostResponseDTO);

        PostResponseDTO result = postService.getPostById(POST_ID, USER_ID);

        assertNotNull(result);
        assertEquals(POST_ID, result.getId());
        assertFalse(result.isLiked());
        verify(likeRepository).existsByPostIdAndUserId(POST_ID, USER_ID);
    }

    @Test
    void getPostById_nonExistentPost_throwsPostNotFoundException() {
        when(postRepository.findById("nonexistent")).thenReturn(Optional.empty());

        PostNotFoundException exception = assertThrows(PostNotFoundException.class,
                () -> postService.getPostById("nonexistent", USER_ID));

        assertTrue(exception.getMessage().contains("nonexistent"));
    }

    // ===== getPostsByUserId tests =====

    @Test
    void getPostsByUserId_returnsPaginatedResults() {
        List<Post> posts = List.of(samplePost);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());

        when(postRepository.findByUserId(eq(USER_ID), any(Pageable.class))).thenReturn(postPage);
        when(likeRepository.existsByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(false);
        when(modelMapper.map(any(Post.class), eq(PostResponseDTO.class))).thenReturn(samplePostResponseDTO);

        Page<PostResponseDTO> result = postService.getPostsByUserId(USER_ID, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        verify(postRepository).findByUserId(eq(USER_ID), any(Pageable.class));
    }

    // ===== deletePost tests =====

    @Test
    void deletePost_byOwner_deletesPostAndLikes() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(samplePost));

        postService.deletePost(USER_ID, POST_ID);

        verify(likeRepository).deleteByPostId(POST_ID);
        verify(postRepository).delete(samplePost);
    }

    @Test
    void deletePost_byNonOwner_throwsUnauthorizedAccessException() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(samplePost));

        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class,
                () -> postService.deletePost(OTHER_USER_ID, POST_ID));

        assertTrue(exception.getMessage().contains("not authorized"));
        verify(postRepository, never()).delete(any(Post.class));
        verify(likeRepository, never()).deleteByPostId(anyString());
    }

    @Test
    void deletePost_nonExistentPost_throwsPostNotFoundException() {
        when(postRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class,
                () -> postService.deletePost(USER_ID, "nonexistent"));

        verify(postRepository, never()).delete(any(Post.class));
    }

    // ===== likePost tests =====

    @Test
    void likePost_firstTime_createsLikeAndIncrementsCount() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(samplePost));
        when(likeRepository.existsByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(false);
        when(likeRepository.save(any(Like.class))).thenReturn(Like.builder()
                .id("like-1").postId(POST_ID).userId(USER_ID).build());
        when(postRepository.save(any(Post.class))).thenReturn(samplePost);

        LikeResponseDTO result = postService.likePost(USER_ID, POST_ID);

        assertNotNull(result);
        assertEquals(POST_ID, result.getPostId());
        assertTrue(result.isLiked());
        assertEquals(1, result.getTotalLikes());

        verify(likeRepository).save(any(Like.class));

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());
        assertEquals(1, postCaptor.getValue().getLikesCount());
    }

    @Test
    void likePost_alreadyLiked_throwsAlreadyLikedException() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(samplePost));
        when(likeRepository.existsByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(true);

        assertThrows(AlreadyLikedException.class,
                () -> postService.likePost(USER_ID, POST_ID));

        verify(likeRepository, never()).save(any(Like.class));
    }

    // ===== unlikePost tests =====

    @Test
    void unlikePost_whenLiked_removesLikeAndDecrementsCount() {
        samplePost.setLikesCount(5);
        Like existingLike = Like.builder()
                .id("like-1").postId(POST_ID).userId(USER_ID).build();

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(samplePost));
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.of(existingLike));
        when(postRepository.save(any(Post.class))).thenReturn(samplePost);

        LikeResponseDTO result = postService.unlikePost(USER_ID, POST_ID);

        assertNotNull(result);
        assertEquals(POST_ID, result.getPostId());
        assertFalse(result.isLiked());
        assertEquals(4, result.getTotalLikes());

        verify(likeRepository).delete(existingLike);

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());
        assertEquals(4, postCaptor.getValue().getLikesCount());
    }

    @Test
    void unlikePost_whenNotLiked_throwsPostNotFoundException() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(samplePost));
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class,
                () -> postService.unlikePost(USER_ID, POST_ID));

        verify(likeRepository, never()).delete(any(Like.class));
    }
}
