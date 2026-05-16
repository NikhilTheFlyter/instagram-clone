package com.instagram.trending.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrendingPostDTO {

    private String id;
    private String postId;
    private String userId;
    private String caption;
    private List<String> mediaUrls;
    private List<String> hashtags;
    private long likesCount;
    private double score;
    private LocalDateTime calculatedAt;
}
