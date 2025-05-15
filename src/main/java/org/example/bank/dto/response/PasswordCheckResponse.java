package org.example.bank.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordCheckResponse {
    private boolean matched;
}
