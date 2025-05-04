package com.example.PetTama.service;

import com.example.PetTama.dto.UserDto;
import com.example.PetTama.entity.User;
import com.example.PetTama.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserDto getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        return UserDto.fromEntity(user);
    }

    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = userDto.toEntity();
        user.setId(null); // Ensure we create a new user
        User savedUser = userRepository.save(user);
        return UserDto.fromEntity(savedUser);
    }

    @Transactional
    public UserDto updateUser(Long userId, UserDto userDto) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        existingUser.setNickname(userDto.getNickname());
        existingUser.setEmail(userDto.getEmail());

        // Only update password if provided
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            existingUser.setPassword(userDto.getPassword());
        }

        // Update monthly income
        existingUser.setMonthlyIncome(userDto.getMonthlyIncome());

        User updatedUser = userRepository.save(existingUser);
        return UserDto.fromEntity(updatedUser);
    }
}