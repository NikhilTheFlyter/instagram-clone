package com.instagram.post.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class TrendingServiceClient {

    private final RestTemplate restTemplate;

    @CircuitBreaker(name = "trendingService", fallbackMethod = "pushToTrendingFallback")
    public void pushToTrending(String postId, String userId, String caption,
                                List<String> mediaUrls, List<String> hashtags, long likesCount) {
        Map<String, Object> body = new HashMap<>();
        body.put("postId", postId);
        body.put("userId", userId);
        body.put("caption", caption);
        body.put("mediaUrls", mediaUrls);
        body.put("hashtags", hashtags);
        body.put("likesCount", likesCount);

        try {
            restTemplate.postForEntity("http://trending-service/api/trending/posts", body, Object.class);
            log.info("Pushed post {} to trending service", postId);
        } catch (Exception e) {
            log.warn("Failed to push post {} to trending: {}", postId, e.getMessage());
        }
    }

    public void pushToTrendingFallback(String postId, String userId, String caption,
                                        List<String> mediaUrls, List<String> hashtags, long likesCount, Throwable t) {
        log.warn("Fallback: trending-service unavailable, skipping push for post {}", postId);
    }

    @CircuitBreaker(name = "trendingService", fallbackMethod = "removeFromTrendingFallback")
    public void removeFromTrending(String postId) {
        try {
            restTemplate.delete("http://trending-service/api/trending/posts/{postId}", postId);
            log.info("Removed post {} from trending", postId);
        } catch (Exception e) {
            log.warn("Failed to remove post {} from trending: {}", postId, e.getMessage());
        }
    }

    public void removeFromTrendingFallback(String postId, Throwable t) {
        log.warn("Fallback: trending-service unavailable, skipping remove for post {}", postId);
    }
}
