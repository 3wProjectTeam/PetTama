package com.example.PetTama.entity;

import com.example.PetTama.dto.PetGetDto;
import com.example.PetTama.fsm.PetFSM;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pet {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @Column(name = "name")
    private String name;

    @Column(name = "pet_type")
    private String petType;  // 새로 추가된 필드: "DOG", "CAT"

    @Column(name = "hp")
    private int hp;

    @Column(name = "fullness")
    private int fullness;

    @Column(name = "tired")
    private int tired;

    @Column(name = "happiness")
    private int happiness;

    @Column(name = "thirsty")
    private int thirsty;

    @Column(name = "stress")
    private int stress;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    /**
     * Converts this Pet entity to a DTO with additional state information
     * @return Enhanced PetGetDto with current state and recommendations
     */
    public PetGetDto toDto(Pet pet) {
        // Create the basic DTO with all stats
        PetGetDto dto = new PetGetDto(
                pet.getId(),
                pet.getName(),
                pet.getPetType(), // petType 필드 추가
                pet.getHp(),
                pet.getFullness(),
                pet.getHappiness(),
                pet.getTired(),
                pet.getThirsty(),
                pet.getStress()
        );

        // Add the current state
        PetFSM.PetState currentState = PetFSM.getCurrentState(pet);
        dto.setState(currentState);

        // Add recommendation based on current state
        String recommendation = PetFSM.getActionRecommendation(pet);
        dto.setRecommendation(recommendation);

        return dto;
    }
}