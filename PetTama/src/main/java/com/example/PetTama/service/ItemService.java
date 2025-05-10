package com.example.PetTama.service;

import com.example.PetTama.dto.ItemDto;
import com.example.PetTama.dto.PetGetDto;
import com.example.PetTama.dto.UserItemDto;
import com.example.PetTama.entity.Item;
import com.example.PetTama.entity.Pet;
import com.example.PetTama.entity.User;
import com.example.PetTama.entity.UserItem;
import com.example.PetTama.repository.ItemRepository;
import com.example.PetTama.repository.PetRepository;
import com.example.PetTama.repository.UserItemRepository;
import com.example.PetTama.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserItemRepository userItemRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;

    public ItemService(ItemRepository itemRepository,
                       UserItemRepository userItemRepository,
                       UserRepository userRepository,
                       PetRepository petRepository) {
        this.itemRepository = itemRepository;
        this.userItemRepository = userItemRepository;
        this.userRepository = userRepository;
        this.petRepository = petRepository;
    }

    // Get all available items
    public List<ItemDto> getAllItems() {
        return itemRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get items by type
    public List<ItemDto> getItemsByType(String itemType) {
        return itemRepository.findByItemType(itemType).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get items by maximum price
    public List<ItemDto> getItemsByMaxPrice(double maxPrice) {
        return itemRepository.findByPriceLessThanEqual(maxPrice).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get items owned by a user
    public List<UserItemDto> getUserItems(Long userId) {
        return userItemRepository.findByUserId(userId).stream()
                .map(this::convertToUserItemDto)
                .collect(Collectors.toList());
    }

    // Purchase an item
    @Transactional
    public UserItemDto purchaseItem(Long userId, Long itemId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found: " + itemId));

        // Check if user already has this item
        UserItem userItem = userItemRepository.findByUserIdAndItemId(userId, itemId);

        if (userItem != null) {
            // Update quantity if user already has the item
            userItem.setQuantity(userItem.getQuantity() + quantity);
        } else {
            // Create new user item if they don't have it yet
            userItem = new UserItem();
            userItem.setUser(user);
            userItem.setItem(item);
            userItem.setQuantity(quantity);
            userItem.setPurchaseDate(LocalDateTime.now());
        }

        userItem = userItemRepository.save(userItem);
        return convertToUserItemDto(userItem);
    }

    // Use an item on a pet
    @Transactional
    public PetGetDto useItem(Long userId, Long petId, Long itemId) {
        // 펫 찾기
        Pet pet = petRepository.findByIdAndUserId(petId, userId);
        if (pet == null) {
            throw new EntityNotFoundException("Pet not found for id: " + petId);
        }

        // 사용자 아이템 찾기
        UserItem userItem = userItemRepository.findByUserIdAndItemId(userId, itemId);
        if (userItem == null || userItem.getQuantity() <= 0) {
            throw new IllegalStateException("Item not available or insufficient quantity");
        }

        // 아이템 정보 가져오기
        Item item = userItem.getItem();

        // 음식 아이템에 대한 쿨타임 체크
        if (item.getItemType().equals("FOOD")) {
            LocalDateTime lastFed = pet.getLastFedTime(); // Pet 엔티티에 이 필드 추가 필요
            LocalDateTime now = LocalDateTime.now();

            if (lastFed != null && Duration.between(lastFed, now).toHours() < 5) {
                long remainingHours = 5 - Duration.between(lastFed, now).toHours();
                long remainingMinutes = 5 * 60 - Duration.between(lastFed, now).toMinutes();

                // 남은 시간이 1시간 미만인 경우 분 단위로 표시
                String timeMessage = remainingHours > 0 ?
                        remainingHours + "시간 후" :
                        (remainingMinutes % 60) + "분 후";

                throw new IllegalStateException("먹이는 5시간에 한 번만 줄 수 있습니다. " +
                        timeMessage + "에 다시 시도하세요.");
            }

            // 먹이 시간 업데이트
            pet.setLastFedTime(now);
        }
        // Apply item effects with price-based multiplier
        double multiplier = item.getEffectMultiplier();

        // Apply effects to pet stats
        pet.setHappiness(Math.min(100, pet.getHappiness() + (int)(item.getHappinessEffect() * multiplier)));
        pet.setFullness(Math.min(100, pet.getFullness() + (int)(item.getFullnessEffect() * multiplier)));
        pet.setThirsty(Math.max(0, pet.getThirsty() - (int)(item.getHydrationEffect() * multiplier)));
        pet.setTired(Math.max(0, pet.getTired() - (int)(item.getEnergyEffect() * multiplier)));
        pet.setStress(Math.max(0, pet.getStress() - (int)(item.getStressReduction() * multiplier)));

        // Update last interaction time
        pet.setLastUpdated(LocalDateTime.now());

        // Decrease item quantity
        userItem.setQuantity(userItem.getQuantity() - 1);
        userItemRepository.save(userItem);

        // Save and return updated pet
        Pet updatedPet = petRepository.save(pet);
        return updatedPet.toDto(updatedPet);
    }

    // Helper methods for DTO conversion
    private ItemDto convertToDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getItemType(),
                item.getHappinessEffect(),
                item.getFullnessEffect(),
                item.getHydrationEffect(),
                item.getEnergyEffect(),
                item.getStressReduction()
        );
    }

    private UserItemDto convertToUserItemDto(UserItem userItem) {
        return new UserItemDto(
                userItem.getId(),
                userItem.getUser().getId(),
                convertToDto(userItem.getItem()),
                userItem.getQuantity(),
                userItem.getPurchaseDate().toString()
        );
    }
}
