package com.instagram.post.dto;

import com.instagram.post.entity.MediaType;
import com.instagram.post.entity.Privacy;
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
public class PostResponseDTO {

    private String id;
    private String userId;
    private String caption;
    private List<String> mediaUrls;
    private MediaType mediaType;
    private List<String> hashtags;
    private List<String> tags;
    private Privacy privacy;
    private long likesCount;
    private boolean liked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
