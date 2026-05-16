package com.instagram.follow.entity;

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
@Document(collection = "follows")
@CompoundIndex(def = "{'followerId': 1, 'followingId': 1}", unique = true)
public class Follow {

    @Id
    private String id;

    @Indexed
    private String followerId;

    @Indexed
    private String followingId;

    @CreatedDate
    private LocalDateTime createdAt;
}
