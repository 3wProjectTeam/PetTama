package com.example.PetTama.dto;

import com.example.PetTama.entity.Comment;
import com.example.PetTama.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private Long postId;
    private String name;
    private String body;

    // Convert entity to DTO
    public static CommentDto fromEntity(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setPostId(comment.getPost().getId());
        dto.setName(comment.getName());
        dto.setBody(comment.getBody());
        return dto;
    }

    // Convert DTO to entity
    public Comment toEntity(Post post) {
        Comment comment = new Comment();
        comment.setId(this.id);
        comment.setPost(post);
        comment.setName(this.name);
        comment.setBody(this.body);
        return comment;
    }
}
