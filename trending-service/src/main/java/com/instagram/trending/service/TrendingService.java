package com.instagram.trending.service;

import com.instagram.trending.dto.TrendingHashtagDTO;
import com.instagram.trending.dto.TrendingPostDTO;
import com.instagram.trending.entity.TrendingHashtag;
import com.instagram.trending.entity.TrendingPost;
import com.instagram.trending.repository.TrendingHashtagRepository;
import com.instagram.trending.repository.TrendingPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrendingService {

    private final TrendingPostRepository trendingPostRepository;
    private final TrendingHashtagRepository trendingHashtagRepository;
    private final ModelMapper modelMapper;

    public Page<TrendingPostDTO> getTrendingPosts(String filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TrendingPost> posts;

        if ("recent".equalsIgnoreCase(filter)) {
            posts = trendingPostRepository.findAllByOrderByCalculatedAtDesc(pageable);
        } else {
            // default and "popular" both order by score
            posts = trendingPostRepository.findAllByOrderByScoreDesc(pageable);
        }

        return posts.map(post -> modelMapper.map(post, TrendingPostDTO.class));
    }

    public List<TrendingHashtagDTO> getTrendingHashtags(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<TrendingHashtag> hashtags = trendingHashtagRepository.findAllByOrderByScoreDesc(pageable);

        return hashtags.getContent().stream()
                .map(h -> modelMapper.map(h, TrendingHashtagDTO.class))
                .collect(Collectors.toList());
    }

    @Scheduled(fixedRate = 60000)
    public void refreshTrending() {
        log.info("Starting trending refresh job...");

        // Recalculate scores for all existing trending posts
        List<TrendingPost> allPosts = trendingPostRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (TrendingPost post : allPosts) {
            long hoursSinceCreated = ChronoUnit.HOURS.between(post.getCalculatedAt(), now);
            double recencyBonus = Math.max(0, 100 - hoursSinceCreated * 5);
            double score = post.getLikesCount() * 2.0 + recencyBonus;
            post.setScore(score);
        }

        trendingPostRepository.saveAll(allPosts);
        log.info("Recalculated scores for {} trending posts", allPosts.size());

        // Recalculate hashtag scores based on post counts and recency
        recalculateHashtagScores(allPosts);

        // Clean up entries older than 7 days
        LocalDateTime cutoff = now.minusDays(7);
        trendingPostRepository.deleteByCalculatedAtBefore(cutoff);
        log.info("Cleaned up trending entries older than {}", cutoff);

        log.info("Trending refresh job completed.");
    }

    private void recalculateHashtagScores(List<TrendingPost> allPosts) {
        // Count posts per hashtag
        Map<String, Long> hashtagCounts = new HashMap<>();
        for (TrendingPost post : allPosts) {
            if (post.getHashtags() != null) {
                for (String hashtag : post.getHashtags()) {
                    hashtagCounts.merge(hashtag, 1L, Long::sum);
                }
            }
        }

        LocalDateTime now = LocalDateTime.now();

        for (Map.Entry<String, Long> entry : hashtagCounts.entrySet()) {
            String tag = entry.getKey();
            long count = entry.getValue();

            Optional<TrendingHashtag> existingOpt = trendingHashtagRepository.findByHashtag(tag);
            TrendingHashtag trendingHashtag;

            if (existingOpt.isPresent()) {
                trendingHashtag = existingOpt.get();
            } else {
                trendingHashtag = TrendingHashtag.builder()
                        .hashtag(tag)
                        .build();
            }

            trendingHashtag.setPostCount(count);
            trendingHashtag.setScore(count * 10.0);
            trendingHashtag.setCalculatedAt(now);

            trendingHashtagRepository.save(trendingHashtag);
        }

        log.info("Recalculated scores for {} hashtags", hashtagCounts.size());
    }

    public TrendingPostDTO addPost(TrendingPostDTO dto) {
        Optional<TrendingPost> existingOpt = trendingPostRepository.findByPostId(dto.getPostId());

        TrendingPost trendingPost;

        if (existingOpt.isPresent()) {
            // Update existing entry
            trendingPost = existingOpt.get();
            trendingPost.setUserId(dto.getUserId());
            trendingPost.setCaption(dto.getCaption());
            trendingPost.setMediaUrls(dto.getMediaUrls());
            trendingPost.setHashtags(dto.getHashtags());
            trendingPost.setLikesCount(dto.getLikesCount());
        } else {
            // Create new entry
            trendingPost = modelMapper.map(dto, TrendingPost.class);
            trendingPost.setId(null); // let MongoDB generate the id
        }

        // Calculate score
        LocalDateTime now = LocalDateTime.now();
        trendingPost.setCalculatedAt(now);
        double recencyBonus = 100.0; // newly added, full bonus
        double score = trendingPost.getLikesCount() * 2.0 + recencyBonus;
        trendingPost.setScore(score);

        TrendingPost saved = trendingPostRepository.save(trendingPost);
        return modelMapper.map(saved, TrendingPostDTO.class);
    }

    public void removePost(String postId) {
        Optional<TrendingPost> existingOpt = trendingPostRepository.findByPostId(postId);
        existingOpt.ifPresent(trendingPostRepository::delete);
    }
}
