package com.example.PetTama.entity;

import com.example.PetTama.dto.PetGetDto;
import com.example.PetTama.fsm.PetFSM;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    private int fullDaysCount = 0;  // 포만감 100 상태를 유지한 일수
    
    @Column(name = "is_obese", nullable = false)
    private boolean obese = false;  // 비만 상태 여부
    
    @Column(name = "high_stress_days_count")
    private int highStressDaysCount = 0;  // 스트레스 80 이상 유지 일수
    
    @Column(name = "is_depressed", nullable = false)
    private boolean depressed = false;  // 우울증 상태 여부
    
    @Column(name = "depression_days_count")
    private int depressionDaysCount = 0;  // 우울증 유지 일수
    
    @Column(name = "last_daily_check")
    private LocalDateTime lastDailyCheck;  // 마지막 일일 상태 확인 시간

    /**
     * 상태별 이미지 경로를 반환하는 메서드
     * @return 이미지 경로 목록
     */
    public List<String> getStateImagePaths() {
        try {
            PetFSM.PetState state = PetFSM.getCurrentState(this);

            // 수면 중인 경우
            if (this.isSleeping()) {
                return Arrays.asList(
                        "/images/" + this.getPetType().toLowerCase() + "_sleep1.png",
                        "/images/" + this.getPetType().toLowerCase() + "_sleep2.png"
                );
            }

            // 산책 중인 경우
            if (this.isWalking()) {
                return Arrays.asList(
                        "/images/" + this.getPetType().toLowerCase() + "_happy1.png",
                        "/images/" + this.getPetType().toLowerCase() + "_happy2.png"
                );
            }

            // 상태에 따른 이미지 경로 결정
            switch (state) {
                case HAPPY:
                    // 행복한 상태일 때 애니메이션을 위한 두 개의 이미지 반환
                    return Arrays.asList(
                            "/images/" + this.getPetType().toLowerCase() + "_happy1.png",
                            "/images/" + this.getPetType().toLowerCase() + "_happy2.png"
                    );
                case HUNGRY:
                    return Arrays.asList(
                            "/images/" + this.getPetType().toLowerCase() + "_tired1.png",
                            "/images/" + this.getPetType().toLowerCase() + "_tired2.png"
                    );
                case TIRED:
                    return Arrays.asList(
                            "/images/" + this.getPetType().toLowerCase() + "_tired1.png",
                            "/images/" + this.getPetType().toLowerCase() + "_tired2.png"
                    );
                case BORED:
                    return Arrays.asList(
                            "/images/" + this.getPetType().toLowerCase() + "_tired1.png",
                            "/images/" + this.getPetType().toLowerCase() + "_tired2.png"
                    );
                case STRESSED:
                    return Arrays.asList(
                            "/images/" + this.getPetType().toLowerCase() + "_sick1.png",
                            "/images/" + this.getPetType().toLowerCase() + "_sick2.png"
                    );
                case THIRSTY:
                    return Arrays.asList(
                            "/images/" + this.getPetType().toLowerCase() + "_tired1.png",
                            "/images/" + this.getPetType().toLowerCase() + "_tired2.png"
                    );
                case SICK:
                    return Arrays.asList(
                            "/images/" + this.getPetType().toLowerCase() + "_sick1.png",
                            "/images/" + this.getPetType().toLowerCase() + "_sick2.png"
                    );
                case CRITICAL:
                    return Arrays.asList(
                            "/images/" + this.getPetType().toLowerCase() + "_sick1.png",
                            "/images/" + this.getPetType().toLowerCase() + "_sick2.png"
                    );
                default:
                    return Arrays.asList(
                            "/images/" + this.getPetType().toLowerCase() + "_happy1.png",
                            "/images/" + this.getPetType().toLowerCase() + "_happy2.png"
                    );
            }
        } catch (Exception e) {
            log.error("이미지 경로 결정 중 오류 발생: petId={}", this.getId(), e);
            // 기본 이미지 경로 반환
            return Collections.singletonList("/images/" + (this.getPetType() != null ? this.getPetType().toLowerCase() : "default") + ".png");
        }
    }

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

            // 상태별 이미지 경로 설정
            List<String> imagePaths = pet.getStateImagePaths();
            dto.setImagePaths(imagePaths);
            dto.setAnimated(imagePaths.size() > 1);

            dto.setLastFedTime(pet.getLastFedTime());
            dto.setSleeping(pet.isSleeping());
            dto.setSleepEndTime(pet.getSleepEndTime());
            dto.setWalking(pet.isWalking());
            dto.setWalkEndTime(pet.getWalkEndTime());
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

        // 기본 이미지 경로 설정
        fallbackDto.setImagePaths(Collections.singletonList("/images/" + pet.getPetType().toLowerCase() + ".png"));
        fallbackDto.setAnimated(false);

        return fallbackDto;
    }
}
