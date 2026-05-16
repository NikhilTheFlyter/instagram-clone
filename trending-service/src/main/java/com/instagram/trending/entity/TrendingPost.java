package com.instagram.trending.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "trending_posts")
public class TrendingPost {

    @Id
    private String id;

    @Indexed(unique = true)
    private String postId;

    private String userId;

    private String caption;

    private List<String> mediaUrls;

    private List<String> hashtags;

    private long likesCount;

    private double score;

    private LocalDateTime calculatedAt;
}
