package org.example.bank.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransactionResponse {
    private String type;
    private Long amount;
    private Long balanceAfter;
    private LocalDateTime createdAt;
}