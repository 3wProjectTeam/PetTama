package com.example.PetTama.service;

import com.example.PetTama.dto.CommentDto;
import com.example.PetTama.entity.Comment;
import com.example.PetTama.entity.Post;
import com.example.PetTama.repository.CommentRepository;
import com.example.PetTama.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));

        return commentRepository.findByPost(post).stream()
                .map(CommentDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CommentDto getComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));
        return CommentDto.fromEntity(comment);
    }

    @Transactional
    public CommentDto createComment(CommentDto commentDto) {
        Post post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + commentDto.getPostId()));

        Comment comment = commentDto.toEntity(post);
        comment.setId(null); // Ensure we're creating a new comment

        Comment savedComment = commentRepository.save(comment);
        return CommentDto.fromEntity(savedComment);
    }

    @Transactional
    public CommentDto updateComment(CommentDto commentDto) {
        Comment existingComment = commentRepository.findById(commentDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentDto.getId()));

        Post post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + commentDto.getPostId()));

        // Update fields
        existingComment.setName(commentDto.getName());
        existingComment.setBody(commentDto.getBody());
        existingComment.setPost(post);

        Comment updatedComment = commentRepository.save(existingComment);
        return CommentDto.fromEntity(updatedComment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException("Comment not found with id: " + commentId);
        }
        commentRepository.deleteById(commentId);
    }
}