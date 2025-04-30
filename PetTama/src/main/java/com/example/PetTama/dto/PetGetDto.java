package com.example.PetTama.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetGetDto {
    private String name;
    private int hp;
    private int fullness;
    private int sleepiness;
    private int happiness;
}
