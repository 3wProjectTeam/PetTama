package com.example.PetTama.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PetTamaViewController {

    @GetMapping("/home")
    public String home() {
        return "forward:/index.html";
    }
    @GetMapping("/welcome")
    public String welcome() {
        return "forward:/home.html";
    }
}
