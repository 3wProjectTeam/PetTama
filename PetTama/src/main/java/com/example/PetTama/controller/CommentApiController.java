package com.example.PetTama.controller;

import com.example.PetTama.dto.CommentDto;
import com.example.PetTama.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentApiController {
    private final CommentService commentService;

    public CommentApiController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getCommentsByPost(@PathVariable Long postId) {
        List<CommentDto> comments = commentService.getCommentsByPost(postId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDto> getComment(@PathVariable Long postId, @PathVariable Long commentId) {
        CommentDto comment = commentService.getComment(commentId);
        return ResponseEntity.ok(comment);
    }

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@PathVariable Long postId, @RequestBody CommentDto commentDto) {
        commentDto.setPostId(postId);
        CommentDto createdComment = commentService.createComment(commentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CommentDto commentDto
    ) {
        commentDto.setPostId(postId);
        commentDto.setId(commentId);
        CommentDto updatedComment = commentService.updateComment(commentDto);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}