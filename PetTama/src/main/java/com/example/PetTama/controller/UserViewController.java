package com.example.PetTama.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserViewController {

    @GetMapping("/register")
    public String registerForm() {
        return "forward:/user-form.html";
    }

    @GetMapping("/{userId}")
    public String userProfile(@PathVariable Long userId) {
        return "forward:/user-profile.html";
    }

    @GetMapping("/{userId}/edit")
    public String userEdit(@PathVariable Long userId) {
        return "forward:/user-form.html";
    }
}
