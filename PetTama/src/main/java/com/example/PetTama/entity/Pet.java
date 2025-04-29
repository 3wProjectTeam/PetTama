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
    @Column(name = "hungry")
    private int hungry;
    @Column(name = "sleep")
    private int sleep;
    @Column(name = "happy")
    private int happy;
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Builder
    public Pet(String name, User user) {
        this.id = user.getId();
        this.name = name;
        this.lastUpdated = LocalDateTime.now();
        this.hp = 80;
        this.hungry = 80;
        this.sleep = 60;
        this.happy = 80;
    }
}
