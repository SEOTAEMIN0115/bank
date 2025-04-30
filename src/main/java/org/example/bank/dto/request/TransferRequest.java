package org.example.bank.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferRequest {
    private Long fromAccountId;
    private Long toAccountId;
    private Long amount;
}
