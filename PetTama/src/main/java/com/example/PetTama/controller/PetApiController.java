package com.example.PetTama.controller;

import com.example.PetTama.dto.PetGetDto;
import com.example.PetTama.service.PetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
