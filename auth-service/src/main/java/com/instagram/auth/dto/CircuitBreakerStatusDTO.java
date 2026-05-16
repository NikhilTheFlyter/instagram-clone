package com.instagram.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CircuitBreakerStatusDTO {

    private String state;
    private long remainingSeconds;
    private String message;
}
