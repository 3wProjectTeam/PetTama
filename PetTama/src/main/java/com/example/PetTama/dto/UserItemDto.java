package com.example.PetTama.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserItemDto {
    private Long id;
    private Long userId;
    private ItemDto item;
    private int quantity;
    private String purchaseDate;
}
