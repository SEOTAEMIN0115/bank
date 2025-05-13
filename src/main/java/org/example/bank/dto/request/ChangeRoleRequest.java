package org.example.bank.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeRoleRequest {
    private Long userId;
    private String newRole; // 예: "USER", "ADMIN"
}
