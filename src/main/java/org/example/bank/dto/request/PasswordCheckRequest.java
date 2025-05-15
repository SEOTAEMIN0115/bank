package org.example.bank.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordCheckRequest {
    private String accountNumber;
    private String password;
}
