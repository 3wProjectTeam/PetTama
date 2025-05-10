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
    private LocalDateTime lastFedTime;  // 새로 추가된 필드

    // 모든 필드가 아닌 기본 필드만 설정하는 생성자 - 이미 존재하는 생성자
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
    }

    // 레거시 생성자 - 호환성 유지
    public PetGetDto(Long id, String name, int hp, int fullness, int happiness, int tired, int thirsty, int stress) {
        this.id = id;
        this.name = name;
        this.petType = "CAT"; // 기본값 설정
        this.hp = hp;
        this.fullness = fullness;
        this.happiness = happiness;
        this.tired = tired;
        this.thirsty = thirsty;
        this.stress = stress;
    }

    // 또 다른 레거시 생성자
    public PetGetDto(String name, int hp, int fullness, int happiness, int tired, int thirsty, int stress) {
        this.name = name;
        this.petType = "CAT"; // 기본값 설정
        this.hp = hp;
        this.fullness = fullness;
        this.happiness = happiness;
        this.tired = tired;
        this.thirsty = thirsty;
        this.stress = stress;
    }
}