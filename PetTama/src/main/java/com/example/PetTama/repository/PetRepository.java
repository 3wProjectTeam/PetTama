package com.example.PetTama.repository;


import com.example.PetTama.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {
    Optional<Pet> findById(Long petId);
    List<Pet> findAllByUserId(Long userId);
}
