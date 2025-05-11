package com.example.PetTama.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InventoryController {
    @GetMapping("/inventory")
    public String showInventory() {
        return "forward:/inventory.html";
    }
}