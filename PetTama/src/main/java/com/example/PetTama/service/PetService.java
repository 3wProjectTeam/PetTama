package com.example.PetTama.service;

import com.example.PetTama.dto.PetGetDto;
import com.example.PetTama.entity.Pet;
import com.example.PetTama.repository.PetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService {
    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public List<PetGetDto> getAllPets(Long userId) {
        return petRepository.findAllByUserId(userId).stream()
                .map(p -> new PetGetDto(
                        p.getName(),
                        p.getHp(),
                        p.getFullness(),
                        p.getSleepiness(),
                        p.getHappiness()
                ))
                .toList();
    }
}
