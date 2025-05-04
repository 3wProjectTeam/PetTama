package com.example.PetTama.dto;

import com.example.PetTama.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String nickname;
    private String password;
    private Integer monthlyIncome;

    // Convert entity to DTO
    public static UserDto fromEntity(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setNickname(user.getNickname());
        dto.setPassword(user.getPassword());
        dto.setMonthlyIncome(user.getMonthlyIncome());
        return dto;
    }

    // Convert DTO to entity
    public User toEntity() {
        User user = new User();
        user.setId(this.id);
        user.setEmail(this.email);
        user.setNickname(this.nickname);
        user.setPassword(this.password);
        user.setMonthlyIncome(this.monthlyIncome);
        return user;
    }
}
