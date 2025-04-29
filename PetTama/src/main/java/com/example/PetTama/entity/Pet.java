package com.example.PetTama.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
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
    private Date lastUpdated;
}
