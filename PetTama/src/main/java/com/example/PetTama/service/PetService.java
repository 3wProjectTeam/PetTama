package com.example.PetTama.service;

import com.example.PetTama.entity.Pet;
import com.example.PetTama.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PetService {
    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }
    /**
    * 이 메소드는 pet의 Id로 Pet을 가져오는 기능을 수행합니다.
    * */
    public Pet findById(Long id) {
        return petRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Pet 을 찾을 수 없습니다 : " + id));
    }
    /**
     * user의 Id로 User의 Pet 모두를 가져오는 메소드 입니다.
     * */
    public List<Pet> getAllPets(Long userId) {
        return petRepository.findAllByUserId(userId);
    }

    public Pet createPet(Long id) {
        return petRepository.findByUserId(id)
                .orElseThrow(() -> new IllegalArgumentException("User를 찾을 수 없습니다"));
    }

    @Transactional
    public Pet feed(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No : " + id));
        pet.setHungry(pet.getHungry() - 20);
        pet.setLastUpdated(LocalDateTime.now());
        return pet;
    }
    @Transactional
    public Pet play(Long Id) {
        Pet pet = petRepository.findById(Id)
                .orElseThrow(() -> new IllegalArgumentException("No : " + Id));
        pet.setHappy(pet.getHappy() + 15);
        pet.setLastUpdated(LocalDateTime.now());
        return pet;
    }
    @Transactional
    public Pet brush(Long Id) {
        Pet pet = petRepository.findById(Id)
                .orElseThrow(() -> new IllegalArgumentException("No : " + Id));
        pet.setHappy(pet.getHappy() + 15);
        pet.setLastUpdated(LocalDateTime.now());
        return pet;
    }
    @Transactional
    public Pet sleep(Long Id) {
        Pet pet = petRepository.findById(Id)
                .orElseThrow(() -> new IllegalArgumentException("No : " + Id));
        pet.setSleep(pet.getSleep() - 20);
        pet.setLastUpdated(LocalDateTime.now());
        return pet;
    }
    @Transactional
    public Pet snack(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No : " + id));
        pet.setHungry(pet.getHungry() - 8);
        pet.setLastUpdated(LocalDateTime.now());
        return pet;
    }
}
