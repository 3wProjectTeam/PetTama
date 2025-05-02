package com.example.PetTama.dto;

import com.example.PetTama.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private String nickname;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Convert entity to DTO
    public static PostDto fromEntity(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setNickname(post.getNickname());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getLocalDateTime());
        return dto;
    }

    // Convert DTO to entity
    public Post toEntity() {
        Post post = new Post();
        post.setId(this.id);
        post.setTitle(this.title);
        post.setContent(this.content);
        post.setNickname(this.nickname);
        post.setCreatedAt(this.createdAt);
        post.setLocalDateTime(this.updatedAt);
        return post;
    }
}
