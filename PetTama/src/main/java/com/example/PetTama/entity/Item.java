package com.example.PetTama.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private String itemType; // FOOD, TOY, ACCESSORY, etc.

    // Effect on pet stats (0-100)
    private int happinessEffect;
    private int fullnessEffect;
    private int hydrationEffect; // for thirsty reduction
    private int energyEffect;    // for tired reduction
    private int stressReduction;

    // Calculates the effect multiplier based on price
    // More expensive items have higher multipliers
    public double getEffectMultiplier() {
        // Base multiplier for cheap items
        double baseMultiplier = 1.0;

        // Tier thresholds (adjust as needed)
        if (price >= 10000) {
            return baseMultiplier * 2.5; // Premium items
        } else if (price >= 5000) {
            return baseMultiplier * 2.0; // Expensive items
        } else if (price >= 2000) {
            return baseMultiplier * 1.5; // Mid-range items
        } else {
            return baseMultiplier;       // Basic items
        }
    }
}