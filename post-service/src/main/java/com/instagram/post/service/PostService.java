package com.instagram.post.service;

import com.instagram.post.dto.CreatePostRequestDTO;
import com.instagram.post.dto.LikeResponseDTO;
import com.instagram.post.dto.PostResponseDTO;
import com.instagram.post.entity.Like;
import com.instagram.post.entity.Post;
import com.instagram.post.exception.AlreadyLikedException;
import com.instagram.post.exception.PostNotFoundException;
import com.instagram.post.exception.UnauthorizedAccessException;
import com.instagram.post.repository.LikeRepository;
import com.instagram.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final ModelMapper modelMapper;

    public PostResponseDTO createPost(String userId, CreatePostRequestDTO dto) {
        Post post = Post.builder()
                .userId(userId)
                .caption(dto.getCaption())
                .mediaUrls(dto.getMediaUrls())
                .mediaType(dto.getMediaType())
                .hashtags(dto.getHashtags())
                .tags(dto.getTags())
                .privacy(dto.getPrivacy())
                .likesCount(0)
                .build();

        Post savedPost = postRepository.save(post);
        log.info("Post created with id: {} by user: {}", savedPost.getId(), userId);

        return mapToPostResponseDTO(savedPost, false);
    }

    public PostResponseDTO getPostById(String postId, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

        boolean liked = likeRepository.existsByPostIdAndUserId(postId, userId);
        return mapToPostResponseDTO(post, liked);
    }

    public Page<PostResponseDTO> getPostsByUserId(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> posts = postRepository.findByUserId(userId, pageable);

        return posts.map(post -> {
            boolean liked = likeRepository.existsByPostIdAndUserId(post.getId(), userId);
            return mapToPostResponseDTO(post, liked);
        });
    }

    public void deletePost(String userId, String postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

        if (!post.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("You are not authorized to delete this post");
        }

        likeRepository.deleteByPostId(postId);
        postRepository.delete(post);
        log.info("Post deleted with id: {} by user: {}", postId, userId);
    }

    public LikeResponseDTO likePost(String userId, String postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

        if (likeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new AlreadyLikedException("User has already liked this post");
        }

        Like like = Like.builder()
                .postId(postId)
                .userId(userId)
                .build();
        likeRepository.save(like);

        post.setLikesCount(post.getLikesCount() + 1);
        postRepository.save(post);

        log.info("Post {} liked by user {}", postId, userId);

        return LikeResponseDTO.builder()
                .postId(postId)
                .liked(true)
                .totalLikes(post.getLikesCount())
                .build();
    }

    public LikeResponseDTO unlikePost(String userId, String postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

        Like like = likeRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new PostNotFoundException("Like not found for this post"));

        likeRepository.delete(like);

        post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
        postRepository.save(post);

        log.info("Post {} unliked by user {}", postId, userId);

        return LikeResponseDTO.builder()
                .postId(postId)
                .liked(false)
                .totalLikes(post.getLikesCount())
                .build();
    }

    public LikeResponseDTO getLikeStatus(String userId, String postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

        boolean liked = likeRepository.existsByPostIdAndUserId(postId, userId);

        return LikeResponseDTO.builder()
                .postId(postId)
                .liked(liked)
                .totalLikes(post.getLikesCount())
                .build();
    }

    public Page<PostResponseDTO> searchPosts(String query, String sort, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts;

        if ("popular".equalsIgnoreCase(sort)) {
            posts = postRepository.findByHashtagsContainingOrderByLikesCountDesc(query, pageable);
        } else if ("recent".equalsIgnoreCase(sort)) {
            posts = postRepository.findByHashtagsContainingOrderByCreatedAtDesc(query, pageable);
        } else {
            posts = postRepository.searchPosts(query, pageable);
        }

        return posts.map(post -> modelMapper.map(post, PostResponseDTO.class));
    }

    private PostResponseDTO mapToPostResponseDTO(Post post, boolean liked) {
        PostResponseDTO dto = modelMapper.map(post, PostResponseDTO.class);
        dto.setLiked(liked);
        return dto;
    }
}
