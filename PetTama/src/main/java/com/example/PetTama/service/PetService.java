package com.example.PetTama.service;

import com.example.PetTama.dto.PetGetDto;
import com.example.PetTama.entity.Pet;
import com.example.PetTama.entity.User;
import com.example.PetTama.repository.PetRepository;
import com.example.PetTama.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class PetService {
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    public PetService(PetRepository petRepository, UserRepository userRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
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

    public PetGetDto getPet(Long userId, Long petId) {
        Pet pet = petRepository.findByIdAndUserId(petId, userId);
        return new PetGetDto(
                pet.getName(),
                pet.getHp(),
                pet.getFullness(),
                pet.getSleepiness(),
                pet.getHappiness()
        );
    }

    @Transactional
    public PetGetDto createPet(Long userId, String name) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        ThreadLocalRandom random = ThreadLocalRandom.current();
        int hp         = random.nextInt(50, 101);   // 50 ~ 100
        int fullness   = random.nextInt(0, 101);    // 0 ~ 100
        int sleepiness = random.nextInt(0, 101);
        int happiness  = random.nextInt(0, 101);

        Pet pet = new Pet();
        pet.setUser(user);
        pet.setName(name);
        pet.setHp(hp);
        pet.setFullness(fullness);
        pet.setSleepiness(sleepiness);
        pet.setHappiness(happiness);
        pet.setLastUpdated(LocalDateTime.now());
        pet = petRepository.save(pet);
        return new PetGetDto(
                pet.getName(),
                pet.getHp(),
                pet.getFullness(),
                pet.getSleepiness(),
                pet.getHappiness()
        );
    }

    @Transactional
    public PetGetDto feed(Long userId, Long petId) {
        Pet pet = petRepository.findByIdAndUserId(petId, userId);
        log.info("Before Feed Pet: " + pet.getFullness());
        pet.setFullness(pet.getFullness() + 20);
        pet.setLastUpdated(LocalDateTime.now());
        pet = petRepository.save(pet);
        log.info("After Feed Pet: " + pet.getFullness());
        return new PetGetDto(
                pet.getName(),
                pet.getHp(),
                pet.getFullness(),
                pet.getSleepiness(),
                pet.getHappiness()
        );
    }

    @Transactional
    public PetGetDto play(Long userId, Long petId) {
        Pet pet = petRepository.findByIdAndUserId(petId, userId);
        pet.setHappiness(pet.getHappiness() + 15);
        pet.setLastUpdated(LocalDateTime.now());
        return new PetGetDto(
                pet.getName(),
                pet.getHp(),
                pet.getFullness(),
                pet.getSleepiness(),
                pet.getHappiness()
        );
    }
    @Transactional
    public PetGetDto brush(Long userId, Long petId) {
        Pet pet = petRepository.findByIdAndUserId(petId, userId);
        pet.setHappiness(pet.getHappiness() + 15);
        pet.setLastUpdated(LocalDateTime.now());
        return new PetGetDto(
                pet.getName(),
                pet.getHp(),
                pet.getFullness(),
                pet.getSleepiness(),
                pet.getHappiness()
        );
    }
    @Transactional
    public PetGetDto sleep(Long userId, Long petId) {
        Pet pet = petRepository.findByIdAndUserId(petId, userId);
        pet.setSleepiness(pet.getSleepiness() - 20);
        pet.setLastUpdated(LocalDateTime.now());
        return new PetGetDto(
                pet.getName(),
                pet.getHp(),
                pet.getFullness(),
                pet.getSleepiness(),
                pet.getHappiness()
        );
    }
    @Transactional
    public PetGetDto snack(Long userId, Long petId) {
        Pet pet = petRepository.findByIdAndUserId(petId, userId);
        pet.setFullness(pet.getFullness() + 5);
        pet.setLastUpdated(LocalDateTime.now());
        return new PetGetDto(
                pet.getName(),
                pet.getHp(),
                pet.getFullness(),
                pet.getSleepiness(),
                pet.getHappiness()
        );
    }
}
