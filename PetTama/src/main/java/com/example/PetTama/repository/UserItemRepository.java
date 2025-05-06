package com.example.PetTama.repository;

import com.example.PetTama.entity.UserItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {
    List<UserItem> findByUserId(Long userId);
    UserItem findByUserIdAndItemId(Long userId, Long itemId);
}
