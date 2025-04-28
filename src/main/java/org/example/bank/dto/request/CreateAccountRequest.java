package org.example.bank.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountRequest {
    private Long userId;
    private String accountNumber;
}
