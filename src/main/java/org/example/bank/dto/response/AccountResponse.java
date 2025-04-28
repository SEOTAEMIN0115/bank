package org.example.bank.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountResponse {
    private Long accountId;
    private String accountNumber;
    private Long balance;
}
