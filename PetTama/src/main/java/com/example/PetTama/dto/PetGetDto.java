package com.example.PetTama.dto;

import com.example.PetTama.fsm.PetFSM.PetState;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetGetDto {
    private Long id;
    private String name;
    private String petType;
    private int hp;
    private int fullness;
    private int happiness;
    private int tired;
    private int thirsty;
    private int stress;
    private PetState state;
    private String recommendation;
    private LocalDateTime lastFedTime;
    private boolean sleeping;
    private LocalDateTime sleepEndTime;
    private boolean walking;
    private LocalDateTime walkEndTime;

    public void setSleeping(Boolean sleeping) {
        this.sleeping = (sleeping != null) ? sleeping : false;
    }
    public void setWalking(Boolean walking) {
        this.walking = (walking != null) ? walking : false;
    }

    public PetGetDto(Long id, String name, String petType, int hp, int fullness, int happiness, int tired, int thirsty, int stress) {
        this.id = id;
        this.name = name;
        this.petType = petType;
        this.hp = hp;
        this.fullness = fullness;
        this.happiness = happiness;
        this.tired = tired;
        this.thirsty = thirsty;
        this.stress = stress;
        this.sleeping = false;
    }
}