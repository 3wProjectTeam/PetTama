package com.example.PetTama.service;

import com.example.PetTama.dto.PetGetDto;
import com.example.PetTama.entity.Pet;
import com.example.PetTama.entity.User;
import com.example.PetTama.fsm.PetFSM;
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
    private final ItemService itemService;

    public PetService(PetRepository petRepository, UserRepository userRepository, ItemService itemService) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.itemService = itemService;
    }

    public List<PetGetDto> getAllPets(Long userId) {
        List<Pet> pets = petRepository.findAllByUserId(userId);
        return pets.stream()
                .map(pet -> {
                    Pet updatedPet = PetFSM.updatePetStatus(pet);
                    petRepository.save(updatedPet);
                    return createEnhancedDto(updatedPet);
                })
                .toList();
    }

    public PetGetDto getPet(Long userId, Long petId) {
        Pet pet = petRepository.findByIdAndUserId(petId, userId);
        if (pet == null) {
            throw new EntityNotFoundException("Pet not found for id: " + petId);
        }
        Pet updatedPet = PetFSM.updatePetStatus(pet);
        petRepository.save(updatedPet);
        return createEnhancedDto(updatedPet);
    }

    @Transactional
    public PetGetDto createPet(Long userId, String name, String petType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        ThreadLocalRandom random = ThreadLocalRandom.current();
        Pet pet = new Pet();
        pet.setUser(user);
        pet.setName(name);
        pet.setPetType(petType); // 펫 타입 설정
        pet.setHp(random.nextInt(50, 101));       // 50 ~ 100
        pet.setFullness(random.nextInt(40, 81));  // 40 ~ 80
        pet.setTired(random.nextInt(20, 51));     // 20 ~ 50
        pet.setHappiness(random.nextInt(40, 81)); // 40 ~ 80
        pet.setThirsty(random.nextInt(20, 51));   // 20 ~ 50
        pet.setStress(random.nextInt(20, 51));    // 20 ~ 50
        pet.setLastUpdated(LocalDateTime.now());
        pet = petRepository.save(pet);
        return createEnhancedDto(pet);
    }

    @Transactional
    public PetGetDto feed(Long userId, Long petId, Long itemId) {
        if (itemId == null) {
            throw new IllegalArgumentException("먹이 아이템이 필요합니다.");
        }
        Pet pet = petRepository.findByIdAndUserId(petId, userId);
        if (pet == null) {
            throw new EntityNotFoundException("Pet not found for id: " + petId);
        }
        return itemService.useItem(userId, petId, itemId);
    }

    @Transactional
    public PetGetDto play(Long userId, Long petId) {
        Pet pet = petRepository.findByIdAndUserId(petId, userId);
        if (pet == null) {
            throw new EntityNotFoundException("Pet not found for id: " + petId);
        }

        // Use the FSM to handle playing
        Pet updatedPet = PetFSM.play(pet);
        petRepository.save(updatedPet);

        return createEnhancedDto(updatedPet);
    }

    @Transactional
    public PetGetDto brush(Long userId, Long petId) {
        Pet pet = petRepository.findByIdAndUserId(petId, userId);
        if (pet == null) {
            throw new EntityNotFoundException("Pet not found for id: " + petId);
        }
        Pet updatedPet = PetFSM.brush(pet);
        petRepository.save(updatedPet);

        return createEnhancedDto(updatedPet);
    }

    @Transactional
    public PetGetDto sleep(Long userId, Long petId) {
        Pet pet = petRepository.findByIdAndUserId(petId, userId);
        if (pet == null) {
            throw new EntityNotFoundException("Pet not found for id: " + petId);
        }

        // Use the FSM to handle sleeping
        Pet updatedPet = PetFSM.sleep(pet);
        petRepository.save(updatedPet);

        return createEnhancedDto(updatedPet);
    }

    @Transactional
    public PetGetDto giveWater(Long userId, Long petId) {
        Pet pet = petRepository.findByIdAndUserId(petId, userId);
        if (pet == null) {
            throw new EntityNotFoundException("Pet not found for id: " + petId);
        }

        // Use the FSM to handle giving water
        Pet updatedPet = PetFSM.giveWater(pet);
        petRepository.save(updatedPet);

        return createEnhancedDto(updatedPet);
    }

    @Transactional
    public PetGetDto snack(Long userId, Long petId) {
        Pet pet = petRepository.findByIdAndUserId(petId, userId);
        if (pet == null) {
            throw new EntityNotFoundException("Pet not found for id: " + petId);
        }

        // Use the FSM to handle giving snack
        Pet updatedPet = PetFSM.giveSnack(pet);
        petRepository.save(updatedPet);

        return createEnhancedDto(updatedPet);
    }

    @Transactional
    public PetGetDto petWalking(Long userId, Long petId) {
        Pet pet = petRepository.findByIdAndUserId(petId, userId);
        if (pet == null) {
            throw new EntityNotFoundException("Pet not found for id: " + petId);
        }

        // Use the FSM to handle walking
        Pet updatedPet = PetFSM.walk(pet);
        petRepository.save(updatedPet);

        return createEnhancedDto(updatedPet);
    }

    /**
     * Creates an enhanced DTO that includes additional information like the current state and recommendations
     */
    private PetGetDto createEnhancedDto(Pet pet) {
        PetGetDto dto = pet.toDto(pet);
        return dto;
    }
}