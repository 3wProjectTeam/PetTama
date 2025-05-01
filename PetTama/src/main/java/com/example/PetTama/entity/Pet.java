package com.example.PetTama.entity;

import com.example.PetTama.dto.PetGetDto;
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

    public PetGetDto toDto(Pet pet) {
        return new PetGetDto(pet.getName(), pet.hp, pet.fullness, pet.tired, pet.happiness, pet.thirsty, pet.getStress());
    }
}
