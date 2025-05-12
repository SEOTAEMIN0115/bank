package org.example.bank.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.bank.entity.User;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String name;
    private String role;

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getRole().name()
        );
    }
}
