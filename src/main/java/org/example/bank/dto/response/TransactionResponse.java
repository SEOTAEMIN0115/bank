package org.example.bank.dto.response;

import java.time.LocalDateTime;

import org.example.bank.entity.Transaction;
import org.example.bank.entity.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransactionResponse {

    private Long id;
    private TransactionType type;
    private Long amount;
    private String senderName;
    private String receiverName;
    private Long balanceAfter;
    private LocalDateTime createdAt;

    public static TransactionResponse from(Transaction tx) {
        return new TransactionResponse(
            tx.getId(),
            tx.getType(),
            tx.getAmount(),
            tx.getSenderName(),
            tx.getReceiverName(),
            tx.getBalanceAfter(),
            tx.getCreatedAt()
        );
    }
}