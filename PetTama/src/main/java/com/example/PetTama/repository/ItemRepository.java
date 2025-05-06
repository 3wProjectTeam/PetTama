package com.example.PetTama.repository;

import com.example.PetTama.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByItemType(String itemType);
    List<Item> findByPriceLessThanEqual(double maxPrice);
}
