package com.instagram.trending.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrendingHashtagDTO {

    private String hashtag;
    private long postCount;
    private double score;
}
