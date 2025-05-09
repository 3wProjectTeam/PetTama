package com.example.PetTama.controller;

import com.example.PetTama.dto.PetGetDto;
import com.example.PetTama.entity.User;
import com.example.PetTama.service.PetService;
import com.example.PetTama.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-nums/")
public class PetApiController {
    private final PetService petSer;
    private final UserService userSer;
    public PetApiController(PetService petSer, UserService userSer) {
        this.petSer = petSer;
        this.userSer = userSer;
    }

    @GetMapping("{userId}")
    public ResponseEntity<List<PetGetDto>> getAllPets(@PathVariable Long userId, Authentication authentication) {
        // 인증된 사용자만 접근 가능 (SecurityConfig에서 이미 제한됨)
        // 추가로 현재 로그인한 사용자의 ID와 요청의 userId가 일치하는지 확인
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userSer.findByEmail(userDetails.getUsername());

        if (!user.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<PetGetDto> pets = petSer.getAllPets(userId);
        return ResponseEntity.ok(pets);
    }

    @GetMapping("{userId}/pets/{petId}")
    public ResponseEntity<PetGetDto> getPet(@PathVariable Long userId, @PathVariable Long petId) {
        PetGetDto dto = petSer.getPet(userId, petId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("{userId}")
    public ResponseEntity<PetGetDto> createPet(
            @PathVariable Long userId,
            @RequestParam String name,
            @RequestParam(required = false, defaultValue = "CAT") String petType) {

        PetGetDto dto = petSer.createPet(userId, name, petType);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("{userId}/pets/{petId}/feed")
    public ResponseEntity<PetGetDto> feed(@PathVariable Long userId, @PathVariable Long petId) {
        PetGetDto dto = petSer.feed(userId, petId);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("{userId}/pets/{petId}/play")
    public ResponseEntity<PetGetDto> play(@PathVariable Long userId, @PathVariable Long petId) {
        PetGetDto dto = petSer.play(userId, petId);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("{userId}/pets/{petId}/brush")
    public ResponseEntity<PetGetDto> brush(@PathVariable Long userId, @PathVariable Long petId) {
        PetGetDto dto = petSer.brush(userId, petId);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("{userId}/pets/{petId}/sleep")
    public ResponseEntity<PetGetDto> sleep(@PathVariable Long userId, @PathVariable Long petId) {
        PetGetDto dto = petSer.sleep(userId, petId);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("{userId}/pets/{petId}/water")
    public ResponseEntity<PetGetDto> giveWater(@PathVariable Long userId, @PathVariable Long petId) {
        PetGetDto dto = petSer.giveWater(userId, petId);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("{userId}/pets/{petId}/snack")
    public ResponseEntity<PetGetDto> giveSnack(@PathVariable Long userId, @PathVariable Long petId) {
        PetGetDto dto = petSer.snack(userId, petId);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("{userId}/pets/{petId}/walk")
    public ResponseEntity<PetGetDto> walk(@PathVariable Long userId, @PathVariable Long petId) {
        PetGetDto dto = petSer.petWalking(userId, petId);
        return ResponseEntity.ok(dto);
    }
}