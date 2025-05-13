package com.example.PetTama;

import com.example.PetTama.entity.Pet;
import com.example.PetTama.entity.User;
import com.example.PetTama.fsm.PetFSM;
import com.example.PetTama.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.example.PetTama.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class PetObesityTest {
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private UserRepository userRepository;
    private Pet testPet;
    private User testUser;

    @BeforeEach
    public void setUp() {
        testPet = new Pet();
        testPet.setUser(testUser);
        testPet.setName("TestPet");
        testPet.setPetType("DOG");
        testPet.setHp(100);
        testPet.setFullness(100);
        testPet.setHappiness(80);
        testPet.setTired(20);
        testPet.setThirsty(20);
        testPet.setStress(20);
        testPet.setLastUpdated(LocalDateTime.now());
        testPet.setFullDaysCount(0);
        testPet.setObese(false);
        testPet = petRepository.save(testPet);

        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setNickname("TestUser");
        testUser.setPassword("password");
        testUser = userRepository.save(testUser);
    }

    @Test
    public void testObesityAfterFiveDays() {
        // Given: 포만감 100인 상태의 펫
        assertFalse(testPet.isObese());
        assertEquals(0, testPet.getFullDaysCount());
        assertEquals(100, testPet.getFullness());

        // When: 1일차 체크
        testPet.setLastDailyCheck(LocalDateTime.now().minusDays(1));  // 하루 전으로 설정하여 체크 가능하게
        Pet updatedPet = PetFSM.checkDailyStatus(testPet);

        // Then: 1일 카운트 증가, 아직 비만 아님
        assertEquals(1, updatedPet.getFullDaysCount());
        assertFalse(updatedPet.isObese());

        // When: 2~4일차 체크
        for (int i = 2; i <= 4; i++) {
            updatedPet.setLastDailyCheck(LocalDateTime.now().minusDays(1));
            PetFSM.checkDailyStatus(updatedPet);

            // Then: i일 카운트 증가, 아직 비만 아님
            assertEquals(i, updatedPet.getFullDaysCount());
            assertFalse(updatedPet.isObese());
        }

        // When: 5일차 체크
        updatedPet.setLastDailyCheck(LocalDateTime.now().minusDays(1));
        updatedPet = PetFSM.checkDailyStatus(updatedPet);

        // Then: 5일 카운트 및 비만 상태 확인
        assertEquals(5, updatedPet.getFullDaysCount());
        assertTrue(updatedPet.isObese());
    }
}
