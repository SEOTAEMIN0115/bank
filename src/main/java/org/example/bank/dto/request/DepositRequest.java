package org.example.bank.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepositRequest {
    private Long accountId;
    private Long amount;
}
