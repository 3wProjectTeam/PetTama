package com.example.PetTama.dto;

import com.example.PetTama.entity.Pet;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetGetDto {
    private String name;
    private int hp;
    private int fullness;
    private int happiness;
    private int tired;
    private int thirsty;
}
