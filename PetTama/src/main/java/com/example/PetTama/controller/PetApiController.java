package com.example.PetTama.controller;

import com.example.PetTama.dto.PetGetDto;
import com.example.PetTama.service.PetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-nums/")
public class PetApiController {
    private final PetService petSer;

    public PetApiController(PetService petSer) {
        this.petSer = petSer;
    }

    @GetMapping("{userId}")
    public ResponseEntity<List<PetGetDto>> getAllPets(@PathVariable Long userId) {
        List<PetGetDto> pets = petSer.getAllPets(userId);
        return ResponseEntity.ok(pets);
    }

    @GetMapping("{userId}/pets/{petId}")
    public ResponseEntity<PetGetDto> getPet(@PathVariable Long userId, @PathVariable Long petId) {
        PetGetDto dto = petSer.getPet(userId, petId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("{userId}")
    public ResponseEntity<PetGetDto> createPet(@PathVariable Long userId, @RequestParam String name) {
        PetGetDto dto = petSer.createPet(userId, name);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("{userId}/pets/{petId}")
    public ResponseEntity<PetGetDto> feed(@PathVariable Long userId, @PathVariable Long petId) {
        PetGetDto dto = petSer.feed(userId, petId);
        return ResponseEntity.ok(dto);
    }
}
