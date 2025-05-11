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

    // boolean 대신 Boolean 래퍼 클래스 사용 또는 기본값 false 지정
    @Column(name = "is_sleeping", nullable = false)
    private boolean sleeping = false;

    @Column(name = "sleep_start_time")
    private LocalDateTime sleepStartTime;

    @Column(name = "sleep_end_time")
    private LocalDateTime sleepEndTime;

    // sleeping 설정 메서드 추가 - null 체크 포함
    public void setSleeping(Boolean sleeping) {
        // null이 전달되면 false로 설정
        this.sleeping = (sleeping != null) ? sleeping : false;
    }

    /**
     * Converts this Pet entity to a DTO with additional state information
     * @return Enhanced PetGetDto with current state and recommendations
     */
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
                // 현재 상태 추가
                PetFSM.PetState currentState = PetFSM.getCurrentState(pet);
                dto.setState(currentState);
            } catch (Exception e) {
                log.error("상태 결정 중 오류: petId={}", pet.getId(), e);
                // 오류 발생 시 기본 상태 설정
                dto.setState(PetFSM.PetState.HAPPY);
            }

            try {
                // 권장 사항 추가
                String recommendation = PetFSM.getActionRecommendation(pet);
                dto.setRecommendation(recommendation);
            } catch (Exception e) {
                log.error("권장 사항 생성 중 오류: petId={}", pet.getId(), e);
                // 오류 발생 시 기본 권장 사항 설정
                dto.setRecommendation("펫을 돌봐주세요.");
            }
            // 마지막 먹이 시간 추가
            dto.setLastFedTime(pet.getLastFedTime());
            // 수면 상태 추가
            dto.setSleeping(pet.isSleeping());
            dto.setSleepEndTime(pet.getSleepEndTime());
            return dto;
        } catch (Exception e) {
            log.error("DTO 변환 중 오류: petId={}", pet.getId(), e);
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
}