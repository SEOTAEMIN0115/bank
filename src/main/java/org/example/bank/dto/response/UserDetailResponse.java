package org.example.bank.dto.response;

import java.time.LocalDateTime;

import org.example.bank.entity.Role;
import org.example.bank.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDetailResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private LocalDateTime createdAt;

    public static UserDetailResponse from(User user) {
        return new UserDetailResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            user.getCreatedAt()
        );
    }
}
