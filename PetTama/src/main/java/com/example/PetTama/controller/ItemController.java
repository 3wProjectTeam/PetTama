package com.example.PetTama.controller;

import com.example.PetTama.dto.ItemDto;
import com.example.PetTama.dto.PetGetDto;
import com.example.PetTama.dto.UserItemDto;
import com.example.PetTama.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItems() {
        List<ItemDto> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/type/{itemType}")
    public ResponseEntity<List<ItemDto>> getItemsByType(@PathVariable String itemType) {
        List<ItemDto> items = itemService.getItemsByType(itemType);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/price/{maxPrice}")
    public ResponseEntity<List<ItemDto>> getItemsByMaxPrice(@PathVariable double maxPrice) {
        List<ItemDto> items = itemService.getItemsByMaxPrice(maxPrice);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserItemDto>> getUserItems(@PathVariable Long userId) {
        List<UserItemDto> userItems = itemService.getUserItems(userId);
        return ResponseEntity.ok(userItems);
    }

    @PostMapping("/purchase/{userId}/{itemId}")
    public ResponseEntity<UserItemDto> purchaseItem(
            @PathVariable Long userId,
            @PathVariable Long itemId,
            @RequestParam(defaultValue = "1") int quantity) {
        UserItemDto userItem = itemService.purchaseItem(userId, itemId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).body(userItem);
    }

    @PostMapping("/use/{userId}/{petId}/{itemId}")
    public ResponseEntity<PetGetDto> useItem(
            @PathVariable Long userId,
            @PathVariable Long petId,
            @PathVariable Long itemId) {
        PetGetDto updatedPet = itemService.useItem(userId, petId, itemId);
        return ResponseEntity.ok(updatedPet);
    }
}