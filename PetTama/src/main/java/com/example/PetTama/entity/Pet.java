package com.example.PetTama.entity;

import com.example.PetTama.dto.PetGetDto;
import com.example.PetTama.fsm.PetFSM;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
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

    @Column(name = "is_sleeping", nullable = false)
    private boolean sleeping = false;

    @Column(name = "sleep_start_time")
    private LocalDateTime sleepStartTime;

    @Column(name = "sleep_end_time")
    private LocalDateTime sleepEndTime;

    @Column(name = "is_walking", nullable = false)
    private boolean walking = false;

    @Column(name = "walk_start_time")
    private LocalDateTime walkStartTime;

    @Column(name = "walk_end_time")
    private LocalDateTime walkEndTime;

    @Column(name = "full_days_count")
    private Integer fullDaysCount = 0;

    @Column(name = "is_obese", nullable = false)
    private boolean obese = false;

    @Column(name = "high_stress_days_count")
    private Integer highStressDaysCount = 0;

    @Column(name = "is_depressed", nullable = false)
    private boolean depressed = false;

    @Column(name = "depression_days_count")
    private Integer depressionDaysCount = 0;

    @Column(name = "last_daily_check")
    private LocalDateTime lastDailyCheck;

    public void setSleeping(boolean sleeping) {
        this.sleeping = sleeping;
        if (!sleeping) {
            // 수면 상태가 해제되면 시간 정보도 함께 초기화
            this.sleepStartTime = null;
            this.sleepEndTime = null;
        }
    }
    
    public void setWalking(boolean walking) {
        this.walking = walking;
        if (!walking) {
            // 산책 상태가 해제되면 시간 정보도 함께 초기화
            this.walkStartTime = null;
            this.walkEndTime = null;
        }
    }

    public PetGetDto toDto(Pet pet) {
        try {
            PetGetDto dto = new PetGetDto(
                    pet.getId(),
                    pet.getName(),
                    pet.getPetType() != null ? pet.getPetType() : "CAT",
                    pet.getHp(),
                    pet.getFullness(),
                    pet.getHappiness(),
                    pet.getTired(),
                    pet.getThirsty(),
                    pet.getStress()
            );

            try {
                PetFSM.PetState currentState = PetFSM.getCurrentState(pet);
                dto.setState(currentState);
            } catch (Exception e) {
                log.error("상태 결정 중 오류: petId={}", pet.getId(), e);
                dto.setState(PetFSM.PetState.HAPPY);
            }

            try {
                String recommendation = PetFSM.getActionRecommendation(pet);
                dto.setRecommendation(recommendation);
            } catch (Exception e) {
                log.error("권장 사항 생성 중 오류: petId={}", pet.getId(), e);
                // 오류 발생 시 기본 권장 사항 설정
                dto.setRecommendation("펫을 돌봐주세요.");
            }
            dto.setLastFedTime(pet.getLastFedTime());
            dto.setSleeping(pet.isSleeping());
            dto.setSleepEndTime(pet.getSleepEndTime());
            dto.setWalking(pet.isWalking());
            dto.setSleepEndTime(pet.getSleepEndTime());
            return dto;
        } catch (Exception e) {
            log.error("DTO 변환 중 오류: petId={}", pet.getId(), e);
            return getPetGetDto(pet);
        }
    }

    private static PetGetDto getPetGetDto(Pet pet) {
        PetGetDto fallbackDto = new PetGetDto();
        fallbackDto.setId(pet.getId());
        fallbackDto.setName(pet.getName());
        fallbackDto.setPetType(pet.getPetType() != null ? pet.getPetType() : "CAT");
        fallbackDto.setHp(pet.getHp());
        fallbackDto.setFullness(pet.getFullness());
        fallbackDto.setHappiness(pet.getHappiness());
        fallbackDto.setTired(pet.getTired());
        fallbackDto.setThirsty(pet.getThirsty());
        fallbackDto.setStress(pet.getStress());
        fallbackDto.setState(PetFSM.PetState.HAPPY);
        fallbackDto.setRecommendation("펫을 돌봐주세요.");
        fallbackDto.setSleeping(false);
        return fallbackDto;
    }
}