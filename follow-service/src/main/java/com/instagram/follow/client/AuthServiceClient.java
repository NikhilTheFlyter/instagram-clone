package com.instagram.follow.client;

import com.instagram.follow.dto.UserSummaryDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthServiceClient {

    private final RestTemplate restTemplate;

    @CircuitBreaker(name = "authService", fallbackMethod = "getUserSummaryFallback")
    public UserSummaryDTO getUserSummary(String userId) {
        try {
            var response = restTemplate.getForEntity(
                "http://auth-service/api/auth/profile/{userId}", java.util.Map.class, userId);
            if (response.getBody() != null) {
                var body = response.getBody();
                return UserSummaryDTO.builder()
                    .id(userId)
                    .username((String) body.get("username"))
                    .fullName((String) body.get("fullName"))
                    .profilePicture((String) body.get("profilePicture"))
                    .build();
            }
        } catch (Exception e) {
            log.warn("Failed to fetch user summary for {}: {}", userId, e.getMessage());
        }
        return UserSummaryDTO.builder().id(userId).build();
    }

    public UserSummaryDTO getUserSummaryFallback(String userId, Throwable t) {
        log.warn("Fallback: auth-service unavailable for user {}", userId);
        return UserSummaryDTO.builder().id(userId).username("user_" + userId.substring(0, Math.min(6, userId.length()))).build();
    }
}
