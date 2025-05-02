package com.example.PetTama.repository;

import com.example.PetTama.entity.Comment;
import com.example.PetTama.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
    List<Comment> findByPostId(Long postId);
}
