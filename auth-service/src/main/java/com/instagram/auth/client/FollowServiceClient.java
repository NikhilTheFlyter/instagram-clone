package com.instagram.auth.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class FollowServiceClient {

    private final RestTemplate restTemplate;

    @CircuitBreaker(name = "followService", fallbackMethod = "getFollowStatsFallback")
    public Map<String, Object> getFollowStats(String userId) {
        @SuppressWarnings("unchecked")
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "http://follow-service/api/follow/{userId}/stats", Map.class, userId);
        Map body = response.getBody();
        if (body != null) {
            return body;
        }
        return Map.of("followerCount", 0, "followingCount", 0);
    }

    public Map<String, Object> getFollowStatsFallback(String userId, Throwable t) {
        log.warn("Fallback: follow-service unavailable for user {}: {}", userId, t.getMessage());
        return Map.of("followerCount", 0, "followingCount", 0);
    }
}
