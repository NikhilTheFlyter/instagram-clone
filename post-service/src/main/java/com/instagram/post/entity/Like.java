package com.instagram.post.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "likes")
@CompoundIndex(def = "{'postId': 1, 'userId': 1}", unique = true)
public class Like {

    @Id
    private String id;

    @Indexed
    private String postId;

    @Indexed
    private String userId;

    @CreatedDate
    private LocalDateTime createdAt;
}
