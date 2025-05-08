package org.example.bank.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.bank.entity.Account;

@Getter
@AllArgsConstructor
public class AccountResponse {
    private Long id;
    private String accountNumber;
    private Long balance;

    public static AccountResponse fromEntity(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance()
        );
    }
}
