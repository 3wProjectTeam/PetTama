package com.example.PetTama.service;

import com.example.PetTama.dto.PostDto;
import com.example.PetTama.entity.Post;
import com.example.PetTama.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional(readOnly = true)
    public List<PostDto> getAllPosts() {
        return postRepository.findAll().stream()
                .map(PostDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostDto getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));
        return PostDto.fromEntity(post);
    }

    @Transactional
    public PostDto createPost(PostDto postDto) {
        Post post = postDto.toEntity();
        post.setId(null); // Ensure we're creating a new post
        post.setCreatedAt(LocalDateTime.now());
        post.setLocalDateTime(LocalDateTime.now());

        Post savedPost = postRepository.save(post);
        return PostDto.fromEntity(savedPost);
    }

    @Transactional
    public PostDto updatePost(Long postId, PostDto postDto) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));

        // Update fields
        existingPost.setTitle(postDto.getTitle());
        existingPost.setContent(postDto.getContent());
        existingPost.setLocalDateTime(LocalDateTime.now());

        Post updatedPost = postRepository.save(existingPost);
        return PostDto.fromEntity(updatedPost);
    }

    @Transactional
    public void deletePost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("Post not found with id: " + postId);
        }
        postRepository.deleteById(postId);
    }
}