package com.example.PetTama.service;

import com.example.PetTama.dto.PetGetDto;
import com.example.PetTama.entity.Pet;
import com.example.PetTama.entity.User;
import com.example.PetTama.fsm.PetFSM;
import com.example.PetTama.repository.PetRepository;
import com.example.PetTama.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class PetService {
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;

    public PetService(PetRepository petRepository, UserRepository userRepository, ItemService itemService) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.itemService = itemService;
    }

    public List<PetGetDto> getAllPets(Long userId) {
        List<Pet> pets = petRepository.findAllByUserId(userId);
        return pets.stream()
                .map(pet -> {
                    Pet updatedPet = PetFSM.updatePetStatus(pet);
                    petRepository.save(updatedPet);
                    return createEnhancedDto(updatedPet);
                })
                .toList();
    }

    public PetGetDto getPet(Long userId, Long petId) {
        Pet pet = petRepository.findByIdAndUserId(petId, userId);
        if (pet == null) {
            throw new EntityNotFoundException("Pet not found for id: " + petId);
        }
        
        // 일일 상태 체크 (비만, 우울증 확인)
        Pet checkedPet = PetFSM.checkDailyStatus(pet);
        
        // 시간 기반 상태 업데이트
        Pet updatedPet = PetFSM.updatePetStatus(checkedPet);

        // 명시적인 상태 확인 및 업데이트
        boolean changed = false;

        // 수면 상태 확인
        if (updatedPet.isSleeping()) {
            LocalDateTime now = LocalDateTime.now();
            if (updatedPet.getSleepEndTime() == null) {
                log.warn("Pet {} has sleeping=true but no end time", updatedPet.getName());
                updatedPet.setSleeping(false);
                changed = true;
            } else if (now.isAfter(updatedPet.getSleepEndTime())) {
                log.info("Pet {} sleep time ended, updating state", updatedPet.getName());
                updatedPet.setSleeping(false);
                updatedPet.setSleepStartTime(null);
                updatedPet.setSleepEndTime(null);
                changed = true;
            }
        }

        // 산책 상태 확인
        if (updatedPet.isWalking()) {
            LocalDateTime now = LocalDateTime.now();
            if (updatedPet.getWalkEndTime() == null) {
                log.warn("Pet {} has walking=true but no end time", updatedPet.getName());
                updatedPet.setWalking(false);
                changed = true;
            } else if (now.isAfter(updatedPet.getWalkEndTime())) {
                log.info("Pet {} walk time ended, updating state", updatedPet.getName());
                updatedPet.setWalking(false);
                updatedPet.setWalkStartTime(null);
                updatedPet.setWalkEndTime(null);
                changed = true;
            }
        }

        // 상태가 변경되었다면 저장
        if (changed) {
            updatedPet = petRepository.save(updatedPet);
            log.info("Updated pet state in database: sleeping={}, walking={}",
                    updatedPet.isSleeping(), updatedPet.isWalking());
        }
        
        // 상태 저장 및 반환
        updatedPet = petRepository.save(updatedPet);
        return createEnhancedDto(updatedPet);
    }

     /**
     * 펫의 수면 상태를 확인하고 필요시 업데이트
      * @param pet 확인할 펫
      */
    private void checkSleepState(Pet pet) {
        try {
            if (pet == null) {
                log.warn("checkSleepState: pet is null");
                return;
            }
            if (pet.isSleeping()) {
                // NullPointerException 방지
                if (pet.getSleepEndTime() == null) {
                    log.warn("수면 종료 시간이 null입니다: petId={}", pet.getId());
                    pet.setSleeping(false); // boolean 타입이므로 false로 설정
                    return;
                }

                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                java.time.LocalDateTime endTime = pet.getSleepEndTime();

                // 수면 시간이 끝났는지 확인
                if (now.isAfter(endTime)) {
                    // 수면 상태 해제
                    pet.setSleeping(false); // boolean 타입이므로 false로 설정
                    pet.setSleepStartTime(null);
                    pet.setSleepEndTime(null);
                    log.info("Pet {} woke up on status check", pet.getName());
                }
            }
        } catch (Exception e) {
            log.error("수면 상태 확인 중 오류: petId={}", pet.getId(), e);
            // 수면 상태 확인 실패 시 기본값으로 설정
            pet.setSleeping(false); // boolean 타입이므로 false로 설정
            pet.setSleepStartTime(null);
            pet.setSleepEndTime(null);
        }
    }
    /**
     * 펫의 산책 상태를 확인하고 필요시 업데이트
     * @param pet 확인할 펫
     */
    private void checkWalkState(Pet pet) {
        try {
            if (pet == null) {
                log.warn("checkWalkState: pet is null");
                return;
            }
            if (pet.isWalking()) {
                if (pet.getWalkEndTime() == null) {
                    log.warn("산책 종료 시간이 null입니다: petId={}", pet.getId());
                    pet.setWalking(false);
                    return;
                }
                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                java.time.LocalDateTime endTime = pet.getWalkEndTime();
                if (now.isAfter(endTime)) {
                    pet.setWalking(false);
                    pet.setWalkStartTime(null);
                    pet.setWalkEndTime(null);
                    log.info("Pet {} finished walking on status check", pet.getName());
                    // 중요: 여기서 pet 저장 추가
                    petRepository.save(pet);
                }
            }
        } catch (Exception e) {
            log.error("산책 상태 확인 중 오류: petId={}", pet.getId(), e);
            pet.setWalking(false);
            pet.setWalkStartTime(null);
            pet.setWalkEndTime(null);
            petRepository.save(pet);
        }
    }

    @Transactional
    public PetGetDto createPet(Long userId, String name, String petType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        ThreadLocalRandom random = ThreadLocalRandom.current();
        Pet pet = new Pet();
        pet.setUser(user);
        pet.setName(name);
        pet.setPetType(petType); // 펫 타입 설정
        pet.setHp(random.nextInt(50, 101));       // 50 ~ 100
        pet.setFullness(random.nextInt(40, 81));  // 40 ~ 80
        pet.setTired(random.nextInt(20, 51));     // 20 ~ 50
        pet.setHappiness(random.nextInt(40, 81)); // 40 ~ 80
        pet.setThirsty(random.nextInt(20, 51));   // 20 ~ 50
        pet.setStress(random.nextInt(20, 51));    // 20 ~ 50
        pet.setLastUpdated(LocalDateTime.now());
        pet.setSleeping(false); // 초기 수면 상태 설정
        pet = petRepository.save(pet);
        return createEnhancedDto(pet);
    }

    @Transactional
    public PetGetDto feed(Long userId, Long petId, Long itemId) {
        if (itemId == null) {
            throw new IllegalArgumentException("먹이 아이템이 필요합니다.");
        }
        Pet pet = petRepository.findByIdAndUserId(petId, userId);
        if (pet == null) {
            throw new EntityNotFoundException("Pet not found for id: " + petId);
        }

        // 수면 상태 확인
        if (pet.isSleeping()) {
            LocalDateTime endTime = pet.getSleepEndTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            throw new IllegalStateException(
                    "펫이 자고 있습니다. " + endTime.format(formatter) + "까지 깨울 수 없습니다.");
        }
        if (pet.isWalking()) {
            LocalDateTime endTime = pet.getWalkEndTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            throw new IllegalStateException(
                    "펫이 산책 중입니다. " + endTime.format(formatter) + "까지 다른 활동을 할 수 없습니다.");
        }
        Pet updatedPet = PetFSM.feed(pet);
        itemService.useItem(userId, petId, itemId);
        return createEnhancedDto(updatedPet);
    }

    @Transactional
    public PetGetDto play(Long userId, Long petId) {
        Pet pet = petRepository.findByIdAndUserId(petId, userId);
        if (pet == null) {
            throw new EntityNotFoundException("Pet not found for id: " + petId);
        }

        // 수면 상태 확인
        if (pet.isSleeping()) {
            LocalDateTime endTime = pet.getSleepEndTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            throw new IllegalStateException(
                    "펫이 자고 있습니다. " + endTime.format(formatter) + "까지 깨울 수 없습니다.");
        }
        if (pet.isWalking()) {
            LocalDateTime endTime = pet.getWalkEndTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            throw new IllegalStateException("펫이 산책 중입니다. " + endTime.format(formatter) + "까지 다른 활동을 할 수 없습니다.");
        }
        Pet updatedPet = PetFSM.play(pet);
        petRepository.save(updatedPet);

        return createEnhancedDto(updatedPet);
    }

    @Transactional
    public PetGetDto brush(Long userId, Long petId) {
        Pet pet = petRepository.findByIdAndUserId(petId, userId);
        if (pet == null) {
            throw new EntityNotFoundException("Pet not found for id: " + petId);
        }

        // 수면 상태 확인
        if (pet.isSleeping()) {
            LocalDateTime endTime = pet.getSleepEndTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            throw new IllegalStateException(
                    "펫이 자고 있습니다. " + endTime.format(formatter) + "까지 깨울 수 없습니다.");
        }
        if (pet.isWalking()) {
            LocalDateTime endTime = pet.getWalkEndTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            throw new IllegalStateException(
                    "펫이 산책 중입니다. " + endTime.format(formatter) + "까지 다른 활동을 할 수 없습니다.");
        }
        Pet updatedPet = PetFSM.brush(pet);
        petRepository.save(updatedPet);

        return createEnhancedDto(updatedPet);
    }

    @Transactional
    public PetGetDto sleep(Long userId, Long petId) {
        Pet pet = petRepository.findByIdAndUserId(petId, userId);
        if (pet == null) {
            throw new EntityNotFoundException("Pet not found for id: " + petId);
        }
        if (pet.isSleeping()) {
            LocalDateTime endTime = pet.getSleepEndTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            throw new IllegalStateException(
                    "펫이 자고 있습니다. " + endTime.format(formatter) + "까지 깨울 수 없습니다.");
        }
        if (pet.isWalking()) {
            LocalDateTime endTime = pet.getWalkEndTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            throw new IllegalStateException(
                    "펫이 산책 중입니다. " + endTime.format(formatter) + "까지 다른 활동을 할 수 없습니다.");
        }
        Pet updatedPet = PetFSM.sleep(pet);
        // 로그 추가 - 저장 전 상태 확인
        log.info("Before save - Pet sleeping: {}, start: {}, end: {}",
                updatedPet.isSleeping(), updatedPet.getSleepStartTime(), updatedPet.getSleepEndTime());

        updatedPet = petRepository.save(updatedPet);
        // 로그 추가 - 저장 후 상태 확인
        log.info("After save - Pet sleeping: {}, start: {}, end: {}",
                updatedPet.isSleeping(), updatedPet.getSleepStartTime(), updatedPet.getSleepEndTime());
        return createEnhancedDto(updatedPet);
    }

    @Transactional
    public PetGetDto giveWater(Long userId, Long petId) {
        Pet pet = petRepository.findByIdAndUserId(petId, userId);
        if (pet == null) {
            throw new EntityNotFoundException("Pet not found for id: " + petId);
        }

        // 수면 상태 확인
        if (pet.isSleeping()) {
            LocalDateTime endTime = pet.getSleepEndTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            throw new IllegalStateException(
                    "펫이 자고 있습니다. " + endTime.format(formatter) + "까지 깨울 수 없습니다.");
        }
        if (pet.isWalking()) {
            LocalDateTime endTime = pet.getWalkEndTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            throw new IllegalStateException(
                    "펫이 산책 중입니다. " + endTime.format(formatter) + "까지 다른 활동을 할 수 없습니다.");
        }
        // Use the FSM to handle giving water
        Pet updatedPet = PetFSM.giveWater(pet);
        petRepository.save(updatedPet);

        return createEnhancedDto(updatedPet);
    }

    @Transactional
    public PetGetDto snack(Long userId, Long petId) {
        Pet pet = petRepository.findByIdAndUserId(petId, userId);
        if (pet == null) {
            throw new EntityNotFoundException("Pet not found for id: " + petId);
        }

        // 수면 상태 확인
        if (pet.isSleeping()) {
            LocalDateTime endTime = pet.getSleepEndTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            throw new IllegalStateException(
                    "펫이 자고 있습니다. " + endTime.format(formatter) + "까지 깨울 수 없습니다.");
        }
        if (pet.isWalking()) {
            LocalDateTime endTime = pet.getWalkEndTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            throw new IllegalStateException(
                    "펫이 산책 중입니다. " + endTime.format(formatter) + "까지 다른 활동을 할 수 없습니다.");
        }
        // Use the FSM to handle giving snack
        Pet updatedPet = PetFSM.giveSnack(pet);
        petRepository.save(updatedPet);

        return createEnhancedDto(updatedPet);
    }

    @Transactional
    public PetGetDto petWalking(Long userId, Long petId) {
        Pet pet = petRepository.findByIdAndUserId(petId, userId);
        if (pet == null) {
            throw new EntityNotFoundException("Pet not found for id: " + petId);
        }

        // 수면 상태 확인
        if (pet.isSleeping()) {
            LocalDateTime endTime = pet.getSleepEndTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            throw new IllegalStateException(
                    "펫이 자고 있습니다. " + endTime.format(formatter) + "까지 깨울 수 없습니다.");
        }
        if (pet.isWalking()) {
            LocalDateTime endTime = pet.getWalkEndTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            throw new IllegalStateException(
                    "펫이 산책 중입니다. " + endTime.format(formatter) + "까지 다른 활동을 할 수 없습니다.");
        }
        // Use the FSM to handle walking
        Pet updatedPet = PetFSM.walk(pet);
        petRepository.save(updatedPet);

        return createEnhancedDto(updatedPet);
    }

    private PetGetDto createEnhancedDto(Pet pet) {
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
            
            // 추가 상태 정보 설정
            dto.setObese(pet.isObese());
            dto.setDepressed(pet.isDepressed());
            
            return dto;
        } catch (Exception e) {
            log.error("DTO 변환 중 오류: petId={}", pet.getId(), e);

            // 폴백 DTO 생성
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
            
            // 기본 추가 상태 설정
            fallbackDto.setObese(pet.isObese());
            fallbackDto.setDepressed(pet.isDepressed());

            // 기본 이미지 경로 설정
            fallbackDto.setImagePaths(Collections.singletonList("/images/" + pet.getPetType().toLowerCase() + ".png"));
            fallbackDto.setAnimated(false);

            return fallbackDto;
        }
    }
}
