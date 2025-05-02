package com.example.PetTama.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/board")
public class BoardViewController {

    @GetMapping
    public String boardList() {
        return "forward:/board.html";
    }

    @GetMapping("/{postId}")
    public String postDetail(@PathVariable Long postId) {
        return "forward:/post-detail.html";
    }

    @GetMapping("/create")
    public String createPost() {
        return "forward:/post-form.html";
    }

    @GetMapping("/edit/{postId}")
    public String editPost(@PathVariable Long postId) {
        return "forward:/post-form.html";
    }
}