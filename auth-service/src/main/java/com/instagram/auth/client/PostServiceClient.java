package com.instagram.auth.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostServiceClient {

    private final RestTemplate restTemplate;

    @CircuitBreaker(name = "postService", fallbackMethod = "getPostCountFallback")
    public long getPostCount(String userId) {
        ResponseEntity<Long> response = restTemplate.getForEntity(
                "http://post-service/api/posts/user/{userId}/count", Long.class, userId);
        return response.getBody() != null ? response.getBody() : 0;
    }

    public long getPostCountFallback(String userId, Throwable t) {
        log.warn("Fallback: post-service unavailable for user {}: {}", userId, t.getMessage());
        return 0;
    }
}
