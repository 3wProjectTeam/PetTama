package com.example.PetTama.dto;

import com.example.PetTama.fsm.PetFSM.PetState;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetGetDto {
    private Long id;
    private String name;
    private int hp;
    private int fullness;
    private int happiness;
    private int tired;
    private int thirsty;
    private int stress;
    
    // Additional fields for enhanced functionality
    private PetState state;
    private String recommendation;
    
    // Constructor that maps from the entity fields
    public PetGetDto(Long id, String name, int hp, int fullness, int happiness, int tired, int thirsty, int stress) {
        this.id = id;
        this.name = name;
        this.hp = hp;
        this.fullness = fullness;
        this.happiness = happiness;
        this.tired = tired;
        this.thirsty = thirsty;
        this.stress = stress;
    }
    
    // Legacy constructor for backward compatibility
    public PetGetDto(String name, int hp, int fullness, int happiness, int tired, int thirsty, int stress) {
        this.name = name;
        this.hp = hp;
        this.fullness = fullness;
        this.happiness = happiness;
        this.tired = tired;
        this.thirsty = thirsty;
        this.stress = stress;
    }
}
