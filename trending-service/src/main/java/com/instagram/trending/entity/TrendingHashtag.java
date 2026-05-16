package com.instagram.trending.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "trending_hashtags")
public class TrendingHashtag {

    @Id
    private String id;

    @Indexed(unique = true)
    private String hashtag;

    private long postCount;

    private double score;

    private LocalDateTime calculatedAt;
}
