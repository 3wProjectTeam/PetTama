package com.example.PetTama.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private double price;
    private String itemType;
    private int happinessEffect;
    private int fullnessEffect;
    private int hydrationEffect;
    private int energyEffect;
    private int stressReduction;
}