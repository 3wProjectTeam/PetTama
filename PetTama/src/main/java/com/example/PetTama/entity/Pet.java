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
    private String petType;

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

    @Column(name = "last_fed_time")
    private LocalDateTime lastFedTime;

    /**
     * Converts this Pet entity to a DTO with additional state information
     * @return Enhanced PetGetDto with current state and recommendations
     */
    public PetGetDto toDto(Pet pet) {
        PetGetDto dto = new PetGetDto(
                pet.getId(),
                pet.getName(),
                pet.getPetType(),
                pet.getHp(),
                pet.getFullness(),
                pet.getHappiness(),
                pet.getTired(),
                pet.getThirsty(),
                pet.getStress()
        );
        // 현재 상태 추가
        PetFSM.PetState currentState = PetFSM.getCurrentState(pet);
        dto.setState(currentState);
        // 권장 사항 추가
        String recommendation = PetFSM.getActionRecommendation(pet);
        dto.setRecommendation(recommendation);
        // 마지막 먹이 시간 추가
        dto.setLastFedTime(pet.getLastFedTime());
        return dto;
    }
}