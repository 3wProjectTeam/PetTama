package com.example.PetTama.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Pet {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    @Column(name = "name")
    private String name;
    @Column(name = "hp")
    private int hp;
    @Column(name = "fullness")
    private int fullness;
    @Column(name = "tired")
    private int tired;
    @Column(name = "happiness")
    private int happiness;
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Builder
    public Pet(String name, User user) {
        this.id = user.getId();
        this.name = name;
        this.lastUpdated = LocalDateTime.now();
        this.hp = 80;
        this.fullness = 80;
        this.tired = 60;
        this.happiness = 80;
    }
}
