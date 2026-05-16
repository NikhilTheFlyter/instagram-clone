package com.instagram.post.dto;

import com.instagram.post.entity.MediaType;
import com.instagram.post.entity.Privacy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequestDTO {

    @NotBlank(message = "Caption is required")
    private String caption;

    private List<String> mediaUrls;

    @NotNull(message = "Media type is required")
    private MediaType mediaType;

    private List<String> hashtags;

    private List<String> tags;

    @Builder.Default
    private Privacy privacy = Privacy.PUBLIC;
}
