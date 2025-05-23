package com.example.PetTama.dto;

import com.example.PetTama.fsm.PetFSM.PetState;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

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

    // 이미지 경로 목록
    private List<String> imagePaths;
    // 애니메이션 필요 여부
    private boolean animated;
    
    // 새로 추가된 필드들
    private boolean obese;          // 비만 상태
    private boolean depressed;      // 우울증 상태
    private int fullDaysCount;      // 포만감 100 유지 일수
    private int highStressDaysCount; // 스트레스 80 이상 유지 일수
    private int depressionDaysCount; // 우울증 유지 일수

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
        this.walking = false;
        this.animated = false;
        this.obese = false;
        this.depressed = false;
        this.fullDaysCount = 0;
        this.highStressDaysCount = 0;
        this.depressionDaysCount = 0;
    }
}
