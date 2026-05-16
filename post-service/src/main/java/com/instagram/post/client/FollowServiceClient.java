package com.instagram.post.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FollowServiceClient {

    private final RestTemplate restTemplate;

    @CircuitBreaker(name = "followService", fallbackMethod = "getFollowingIdsFallback")
    public List<String> getFollowingIds(String userId) {
        ResponseEntity<List<String>> response = restTemplate.exchange(
                "http://follow-service/api/follow/{userId}/following/ids",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<String>>() {},
                userId);
        return response.getBody() != null ? response.getBody() : List.of();
    }

    public List<String> getFollowingIdsFallback(String userId, Throwable t) {
        log.warn("Fallback: follow-service unavailable for user {}: {}", userId, t.getMessage());
        return List.of();
    }
}
